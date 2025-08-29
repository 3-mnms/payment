package com.teckit.payment.service;


import com.teckit.payment.dto.request.PayByTekcitPayDTO;
import com.teckit.payment.dto.request.SettlementCommandDTO;
import com.teckit.payment.dto.response.PaymentStatusDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.entity.TekcitPayAccount;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.kafka.producer.PaymentSettlementProducer;
import com.teckit.payment.kafka.producer.PaymentStatusProducer;
import com.teckit.payment.repository.TekcitPayAccountRepository;
import com.teckit.payment.util.PaymentOrderStatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.awt.print.Pageable;


@Service
@RequiredArgsConstructor
public class TekcitPayAccountService {
    private final TekcitPayAccountRepository tekcitPayAccountRepository;
    private final PaymentOrderService paymentOrderService;

    private final PaymentStatusProducer paymentStatusProducer;
    private final PaymentSettlementProducer paymentSettlementProducer;


    public TekcitPayAccountResponseDTO getTekcitPayAccountById(Long id) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));
        return TekcitPayAccountResponseDTO.fromEntity(tekcitPayAccount);
    }

    @Transactional
    public void createTekcitPayAccount(Long id,Long password) {
        tekcitPayAccountRepository.save(TekcitPayAccount.builder()
                .userId(id)
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
        if(dto.getAmount()<=0)
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);


        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        if(!tekcitPayAccount.getPassword().equals(dto.getPassword())) {
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

        if(!prefix.equals("POINT_PAYMENT"))
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);

        if(!paymentOrder.getAmount().equals(dto.getAmount()))
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);

        decreaseAvailableBalance(paymentOrder.getBuyerId(), paymentOrder.getAmount());

        paymentOrderService.changeStatus(paymentOrder, PaymentOrderStatus.POINT_PAYMENT_PAID);

        // 5) 커밋 후 사이드이펙트 발행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                paymentStatusProducer.send(PaymentStatusDTO.builder()
                        .method("payment")
                        .reservationNumber(paymentOrder.getBookingId())
                        .success(true)
                        .build());
                paymentSettlementProducer.send(SettlementCommandDTO.fromPaymentOrder(paymentOrder));
            }
        });
    }
    public  void decreaseAvailableBalance(Long userId, Long chargedAmount) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        tekcitPayAccount.setAvailableBalance(Math.subtractExact(tekcitPayAccount.getAvailableBalance(), chargedAmount));
        tekcitPayAccountRepository.save(tekcitPayAccount);
    }

    public Page<PaymentOrder> getTekcitPayHistory(Long userId,int page,int size){
        boolean isTekcitPayRegistered = tekcitPayAccountRepository.existsById(userId);

        if(!isTekcitPayRegistered) throw new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT);

        return paymentOrderService.getTekcitPayHistory(userId, page, size);
    }

}

