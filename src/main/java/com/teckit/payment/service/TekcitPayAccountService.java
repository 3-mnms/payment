package com.teckit.payment.service;


import com.teckit.payment.dto.request.*;
import com.teckit.payment.dto.response.PaymentStatusDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.entity.TekcitPayAccount;
import com.teckit.payment.enumeration.PayMethodType;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.enumeration.PaymentType;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.kafka.producer.PaymentSettlementProducer;
import com.teckit.payment.kafka.producer.PaymentStatusProducer;
import com.teckit.payment.repository.TekcitPayAccountRepository;
import com.teckit.payment.util.PaymentOrderStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class TekcitPayAccountService {
    private final TekcitPayAccountRepository tekcitPayAccountRepository;

    private final PaymentOrderService paymentOrderService;
    private final WalletService walletService;
    private final LedgerService ledgerService;

    private final PaymentStatusProducer paymentStatusProducer;
    private final PaymentSettlementProducer paymentSettlementProducer;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public void transferToAnotherPerson(TransferRequestDTO dto, Long buyerId) {
        if (dto.getSellerId().equals(buyerId))
            throw new BusinessException(ErrorCode.EQUALS_SELLER_BUYER);

//        1. sellerId와 buyerId가 테킷 페이에 가입되어 있는지 확인
//      구매자 테킷 페이 계좌
        TekcitPayAccount buyerAccount = tekcitPayAccountRepository.findByIdForUpdate(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        if (buyerAccount.getAvailableBalance() < dto.getTotalAmount()) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }

//      판매자 테킷 페이 계좌
        TekcitPayAccount sellerAccount = tekcitPayAccountRepository.findByIdForUpdate(dto.getSellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

//        관리자 테킷 페이 계좌
//        없다는게 말이 안되기는 함
        TekcitPayAccount managerAccount = tekcitPayAccountRepository.findByIdForUpdate(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

//        2. bookingId 기반으로 PaymentOrder에 저장된 값과 요청으로 온 가격이 같은지 확인
        PaymentOrder previousPaymentOrder = paymentOrderService.getPaymentOrderByPaymentId(dto.getPaymentId());
        if (!previousPaymentOrder.getAmount().equals(dto.getTotalAmount()))
            throw new BusinessException(ErrorCode.INVALID_TRANSFER_AMOUNT);

        long expected = Math.floorDiv(previousPaymentOrder.getAmount() + 9L, 10L);

        log.info("입력 Commision : {}", dto.getCommission());
        log.info("수수료 금액 : {}", expected);

        if (dto.getTotalAmount() <= 0 || expected != dto.getCommission())
            throw new BusinessException(ErrorCode.INVALID_TRANSFER_AMOUNT);


//        모든 오류가 발생하면 TekcitPay Failed 됐다라는 이벤트 요청 발생
//
//        paymentOrder 생성
//        타인
        PaymentOrder newUserPaymentOrder = createNewPaymentOrder(dto, dto.getTotalAmount(), buyerId, dto.getSellerId(), previousPaymentOrder);
//        관리자
        PaymentOrder newManagerPaymentOrder = createNewPaymentOrder(dto, dto.getCommission(), buyerId, 1L, previousPaymentOrder);

        paymentOrderService.saveAll(List.of(newUserPaymentOrder, newManagerPaymentOrder));

//         buyerId 테킷 페이 값 차감
        buyerAccount.setAvailableBalance(buyerAccount.getAvailableBalance() - (dto.getTotalAmount() + dto.getCommission()));
        sellerAccount.setAvailableBalance(sellerAccount.getAvailableBalance() + dto.getTotalAmount());
        managerAccount.setAvailableBalance(managerAccount.getAvailableBalance() + dto.getCommission());

//         sellerId 테킷 페이 값 증가
        walletService.saveAndUpdateWallet(WalletDTO.fromEntity(newUserPaymentOrder));
        walletService.saveAndUpdateWallet(WalletDTO.fromEntity(newManagerPaymentOrder));

        ledgerService.saveLedgerAndUpdateLedgerUpdated(LedgerDTO.fromEntity(newUserPaymentOrder), false);
        ledgerService.saveLedgerAndUpdateLedgerUpdated(LedgerDTO.fromEntity(newManagerPaymentOrder), false);

        newUserPaymentOrder.setLedgerUpdated(true);
        newUserPaymentOrder.setWalletUpdated(true);
        newUserPaymentOrder.setPaymentOrderStatus(PaymentOrderStatus.TRANSFER_PAID);

        newManagerPaymentOrder.setWalletUpdated(true);
        newManagerPaymentOrder.setLedgerUpdated(true);
        newManagerPaymentOrder.setPaymentOrderStatus(PaymentOrderStatus.TRANSFER_PAID);

        paymentOrderService.saveAll(List.of(newUserPaymentOrder, newManagerPaymentOrder));

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paymentEventProducer.send(PaymentEventMessageDTO.fromPaymentOrder(newUserPaymentOrder));
                paymentEventProducer.send(PaymentEventMessageDTO.fromPaymentOrder(newManagerPaymentOrder));
                paymentStatusProducer.send(PaymentStatusDTO.builder()
                        .method("transfer")
                        .success(true)
                        .reservationNumber(dto.getBookingId())
                        .build());
            }
        });
    }

    //    14
    @Transactional
    public PaymentOrder createNewPaymentOrder(TransferRequestDTO dto, Long amount, Long buyerId, Long sellerId, PaymentOrder paymentOrder) {
        return PaymentOrder.builder()
                .paymentId(UUID.randomUUID().toString())
                .amount(amount)
                .bookingId(dto.getBookingId())
                .buyerId(buyerId)
                .sellerId(sellerId)
                .currency("KRW")
                .festivalId(paymentOrder.getFestivalId())
                .lastUpdatedAt(LocalDateTime.now())
                .ledgerUpdated(false)
                .walletUpdated(false)
                .payMethod(PayMethodType.POINT_PAYMENT)
                .paymentOrderStatus(PaymentOrderStatus.TRANSFER_READY)
                .paymentType(PaymentType.TRANSFER)
                .build();
    }


    public TekcitPayAccountResponseDTO getTekcitPayAccountById(Long id) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));
        return TekcitPayAccountResponseDTO.fromEntity(tekcitPayAccount);
    }

    @Transactional
    public void createTekcitPayAccount(Long id, String password) {
        tekcitPayAccountRepository.save(TekcitPayAccount.builder()
                .userId(id)
                .password(password)
                .availableBalance(0L)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseAvailableBalance(Long userId, Long chargedAmount) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        tekcitPayAccount.setAvailableBalance(Math.addExact(tekcitPayAccount.getAvailableBalance(), chargedAmount));
        tekcitPayAccountRepository.save(tekcitPayAccount);
    }

    @Transactional
    public void payByTekcitPay(Long userId, PayByTekcitPayDTO dto) {
        if (dto.getAmount() <= 0)
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);


        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        if (!tekcitPayAccount.getPassword().equals(dto.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

//        사용자 계정이랑 일치하지 않을 때,
        if (!tekcitPayAccount.getUserId().equals(userId))
            throw new BusinessException(ErrorCode.NOT_EQUAL_BUYER_ID_AND_USER_ID);


//        금액보다 적을 때,
        if (tekcitPayAccount.getAvailableBalance() < dto.getAmount())
            throw new BusinessException(ErrorCode.NOT_ENOUGH_AVAILABLE_TEKCIT_PAY_POINT);


        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderByPaymentId(dto.getPaymentId());

        String prefix = PaymentOrderStatusUtil.extractPrefix(paymentOrder.getPaymentOrderStatus());

        if (!prefix.equals("POINT_PAYMENT"))
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);

        if (!paymentOrder.getAmount().equals(dto.getAmount()))
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);

        decreaseAvailableBalance(paymentOrder.getBuyerId(), paymentOrder.getAmount());

        paymentOrderService.changeStatus(paymentOrder, PaymentOrderStatus.POINT_PAYMENT_PAID);

        // 5) 커밋 후 사이드이펙트 발행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paymentStatusProducer.send(PaymentStatusDTO.builder()
                        .method("payment")
                        .reservationNumber(paymentOrder.getBookingId())
                        .success(true)
                        .build());
                paymentSettlementProducer.send(SettlementCommandDTO.fromPaymentOrder(paymentOrder));
            }
        });
    }

    public void decreaseAvailableBalance(Long userId, Long chargedAmount) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        tekcitPayAccount.setAvailableBalance(Math.subtractExact(tekcitPayAccount.getAvailableBalance(), chargedAmount));
        tekcitPayAccountRepository.save(tekcitPayAccount);
    }

    public Page<PaymentOrder> getTekcitPayHistory(Long userId, int page, int size) {
        boolean isTekcitPayRegistered = tekcitPayAccountRepository.existsById(userId);

        if (!isTekcitPayRegistered) throw new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT);

        return paymentOrderService.getTekcitPayHistory(userId, page, size);
    }

}

