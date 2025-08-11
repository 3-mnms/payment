package com.teckit.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.teckit.payment.config.PortOneClient;
import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
import com.teckit.payment.dto.request.SettlementCommandDTO;
import com.teckit.payment.dto.response.PortoneSingleResponseDTO;
import com.teckit.payment.entity.Ledger;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.entity.Wallet;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.kafka.producer.SettlementProducer;
import com.teckit.payment.repository.LedgerRepository;
import com.teckit.payment.repository.PaymentEventRepository;
import com.teckit.payment.repository.PaymentOrderRepository;
import com.teckit.payment.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


//buyer Id랑 판매자 아이디만 받아오면 될 ㄷ스

@Service
@AllArgsConstructor
@Slf4j
public class PaymentOrchestrationService {
    //    repository
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final LedgerRepository ledgerRepository;
    private final WalletRepository walletRepository;
    //    service
    private final PaymentOrderService paymentOrderService; // 상태 변경 로직 분리 시 사용
    private final PaymentEventService paymentEventService;


    //    event
    PaymentEventProducer paymentEventProducer;
    SettlementProducer settlementProducer;

//    RestClient

    PortOneClient portOneClient;
    private final EntityManager em;

    @Transactional
    public void completeConfirm(String paymentId){
        PaymentOrder po=paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

        PortoneSingleResponseDTO res = portOneClient.getPayment(paymentId);

        if(res==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID);
        }
        log.info("✅ [PortOne] 응답 값 : {}",res);

        if(!"Paid".equalsIgnoreCase(res.getStatus())){
            throw new BusinessException(ErrorCode.NOT_INVALID_PAYMNET_BY_PORTONE);
        }

        Long total = (res.getAmount() != null) ? res.getAmount().getPaid() : null;

        if (!Objects.equals(po.getAmount(), total) ||
                (res.getCurrency() == null || !po.getCurrency().equalsIgnoreCase(res.getCurrency()))) {
            throw new BusinessException(ErrorCode.NOT_EQUAL_CURRENCY_OR_AMOUNT);
        }

        em.refresh(po);

        log.info("");
        if(!po.isLedgerUpdated()||!po.isWalletUpdated()){
            throw new BusinessException(ErrorCode.NOT_SETTLED_PAYMENT);
        }
    }


    /**
     * 결제 요청 들어왔을 때
     * 이벤트 저장
     * PaymentOrder 없으면 생성
     */
    @Transactional
    public void handlePaymentRequested(PaymentEventDTO dto) {
//        paymentEvent 저장
        paymentEventService.savePaymentEvent(dto);
        paymentOrderRepository.findByPaymentId(dto.getPaymentId())
                .orElseGet(() -> paymentOrderService.createIfAbsent(dto));
    }

    /**
     * 웹훅 성공/실패 들어왔을 때 상태 반영 + 필요 시 정산 이벤트 발행
     */
    @Transactional
    public void handleWebhook(PortoneWebhookDTO payload) {
//        payload에서 값 추출
        final String paymentId = payload.getPayment_id();
        final String txId=payload.getTx_id();
        final PaymentOrderStatus status = payload.getStatus();

        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

        paymentOrderService.changeStatus(paymentOrder, status);
        paymentOrderService.updateTxIdIfAbsent(paymentOrder,txId);

//        Paymnet Event 생성
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
            public void afterCommit() {
            PaymentEventDTO paymentEventDTO = PaymentEventDTO.builder()
                    .paymentId(paymentId)
                    .festivalId(paymentOrder.getFestivalId())
                    .eventType(paymentOrder.getPaymentOrderStatus())
                    .amount(paymentOrder.getAmount())
                    .sellerId(paymentOrder.getSellerId())
                    .currency(paymentOrder.getCurrency())
                    .payMethod(paymentOrder.getPayMethod())
                    .build();

//            paymentEvent 추가 발행
            paymentEventProducer.send(paymentEventDTO);

//            결제 완료 됐을 때, Legder, Wallet 갱신 이벤트 발행
            if (PaymentOrderStatus.Paid.equals(paymentOrder.getPaymentOrderStatus())) {
                settlementProducer.send(buildSettlementCmd(paymentOrder));
            }
        }
        });
    }

    private SettlementCommandDTO buildSettlementCmd(PaymentOrder paymentOrder) {
        return SettlementCommandDTO.builder()
                .sellerId(paymentOrder.getSellerId())
                .buyerId(paymentOrder.getBuyerId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .paymentId(paymentOrder.getPaymentId())
                .txId(paymentOrder.getTxId())
                .build();
    }

    //    enum 형식으로 바꾸기
//    private PaymentOrderStatus changeStatusForm(String status) {
//        if (status == null) throw new IllegalArgumentException("null status occured");
//
//        return switch (status) {
//            case "Paid" -> PaymentOrderStatus.Paid;
//            case "Failed" -> PaymentOrderStatus.Failed;
//            case "Cancelled" -> PaymentOrderStatus.Cancelled;
//            default -> throw new IllegalArgumentException("Unknown status: " + status);
//        };
//    }

    @Transactional
    public void paymentComplete(SettlementCommandDTO dto){
        final String paymentId = dto.getPaymentId();
        final String txId = dto.getTxId();
        final String sellerId = dto.getSellerId();
        final String buyerId = dto.getBuyerId();
        final Long amount = dto.getAmount();
        final String currency = dto.getCurrency();

        PaymentOrder order = paymentOrderRepository.findByPaymentIdForUpdate(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

        if (order.isLedgerUpdated() &&order.isWalletUpdated()) return;

        try {
            ledgerRepository.saveAll(List.of(
                    Ledger.builder().transactionType("DEBIT").userId(buyerId)
                            .amount(amount).currency(currency).paymentId(paymentId).txId(txId).build(),
                    Ledger.builder().transactionType("CREDIT").userId(sellerId)
                            .amount(amount).currency(currency).paymentId(paymentId).txId(txId).build()
            ));
            order.setLedgerUpdated(true);
        } catch (DataIntegrityViolationException e) {
            // 이미 존재 → 멱등 처리로 간주
            order.setLedgerUpdated(true);
        }
//        둘 다 있을 때는 ㄱㅊ


//        PaymentOrder ledger_updated true로 바꿔야 됨

        Wallet buyer  = walletRepository.findById(buyerId).orElseGet(() -> new Wallet(buyerId));
        Wallet seller = walletRepository.findById(sellerId).orElseGet(() -> new Wallet(sellerId));

        buyer.setTotalPaidAmount(   nz(buyer.getTotalPaidAmount())    + amount);
        seller.setTotalReceivedAmount(nz(seller.getTotalReceivedAmount()) + amount);

        walletRepository.save(buyer);
        walletRepository.save(seller);

        order.setWalletUpdated(true);
    }

    private long nz(Long v) { return v == null ? 0L : v; }

}
