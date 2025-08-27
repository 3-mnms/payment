package com.teckit.payment.service;

import com.teckit.payment.config.PortOneClient;
import com.teckit.payment.dto.request.*;
import com.teckit.payment.dto.response.*;
import com.teckit.payment.entity.Ledger;
import com.teckit.payment.entity.PaymentCancellation;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.entity.Wallet;
import com.teckit.payment.enumeration.CancellationStatus;
import com.teckit.payment.enumeration.LedgerTransactionStatus;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.kafka.producer.*;
import com.teckit.payment.repository.LedgerRepository;
import com.teckit.payment.repository.PaymentCancellationRepository;
import com.teckit.payment.repository.PaymentOrderRepository;
import com.teckit.payment.repository.WalletRepository;
import com.teckit.payment.util.PaymentOrderStatusUtil;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Locale;
import java.util.Objects;


//buyer Id랑 판매자 아이디만 받아오면 될 ㄷ스

@Service
@AllArgsConstructor
@Slf4j
public class PaymentOrchestrationService {
    //    repository
    private final PaymentOrderRepository paymentOrderRepository;
    private final LedgerRepository ledgerRepository;
    private final WalletRepository walletRepository;
    private final PaymentCancellationRepository paymentCancellationRepository;
    //    service
    private final PaymentOrderService paymentOrderService; // 상태 변경 로직 분리 시 사용
    private final PaymentEventService paymentEventService;
    private final WalletService walletService;
    private final LedgerService ledgerService;
    private final TekcitPayAccountService tekcitPayAccountService;


    //    event
    PaymentEventProducer paymentEventProducer;
    PaymentSettlementProducer paymentSettlementProducer;
    PaymentCompleteConfirmProducer paymentCompleteConfirmProducer;
    PaymentStatusProducer paymentStatusProducer;
    PaymentCancelProducer paymentCancelProducer;
    PaymentRequestProducer paymentRequestProducer;

    //    RestClient
    PortOneClient portOneClient;
//    private final EntityManager em;

//    @Transactional
//    public void paymentCancel(String paymentId, Long userId) {
////        1. paymentOrder에서 paymentId에 해당하는 entity가 존재하는지 확인
//
//        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentId(paymentId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));
//
//        Long buyerId = paymentOrder.getBuyerId();
//        Long sellerId = paymentOrder.getSellerId();
//
//        Long amount = paymentOrder.getAmount();
//
//        if (!buyerId.equals(userId)) {
//            throw new BusinessException(ErrorCode.NOT_EQUAL_BUYER_ID_AND_USER_ID);
//        }
////        2. 결제 단건 조회 후 포트원 쪽이랑 Paid 됐는지 교차확인
//        PortoneSingleResponseDTO res = portOneClient.getPayment(paymentId);
//        if (!paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.Payment_Paid) || !res.getStatus().equals("PAID")
//        ) {
//            throw new BusinessException(ErrorCode.NOT_PAID_ORDER);
//        }
//
////        3. 환불 준비 완료 이벤트 저장
//        PaymentEventMessageDTO paymentEventMessageDTO = PaymentEventMessageDTO.fromPaymentOrder(paymentOrder);
//        paymentEventProducer.send(paymentEventMessageDTO);
//
////       5. 환불 요청
//        ResponseEntity<PaymentCancelResponseDTO> cancelRes = portOneClient.cancelPayment(paymentId);
//        log.info("cancelRes : {}", cancelRes.getBody().toString());
//
//        boolean isCancelled = cancelRes.getStatusCode().is2xxSuccessful();
//
//        if (isCancelled) {
//            CancellationDTO dto = cancelRes.getBody().getCancellation();
//
//            // 2. PaymentCancellation 엔티티 저장
//            PaymentCancellation cancellation = PaymentCancellation.builder()
//                    .order(paymentOrder)
//                    .externalCancelId(dto.getId())
//                    .pgCancellationId(dto.getPgCancellationId())
//                    .status(CancellationStatus.SUCCEEDED)
//                    .amount(dto.getTotalAmount())
//                    .taxFreeAmount(dto.getTaxFreeAmount())
//                    .vatAmount(dto.getVatAmount())
//                    .reason(dto.getReason())
//                    .trigger(dto.getTrigger())
//                    .receiptUrl(dto.getReceiptUrl())
//                    .requestedAt(dto.getRequestedAt().toInstant())
//                    .cancelledAt(dto.getCancelledAt().toInstant())
//                    .build();
//
//            paymentCancellationRepository.save(cancellation);
//
//            Wallet buyerWallet = walletRepository.findById(buyerId)
//                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
//            Wallet sellerWallet = walletRepository.findById(sellerId)
//                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
//
//            buyerWallet.setTotalPaidAmount(buyerWallet.getTotalPaidAmount() - amount);
//            sellerWallet.setTotalPaidAmount(sellerWallet.getTotalReceivedAmount() - amount);
//
//            LedgerDTO ledgerDTO = LedgerDTO.fromEntity(paymentOrder);
//
//            ledgerService.saveLedgerAndUpdateLedgerUpdated(ledgerDTO, true);
//            paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.Payment_Cancelled);
//            paymentOrderRepository.save(paymentOrder);
//
//            paymentCancelProducer.send(PaymentCancelEventDTO.builder()
//                    .method("cancel")
//                    .reservationNumber(paymentId)
//                    .success(true)
//                    .build());
//        } else {
//            paymentCancelProducer.send(PaymentCancelEventDTO.builder()
//                    .method("cancel")
//                    .reservationNumber(paymentId)
//                    .success(false)
//                    .build());
//        }
//        log.info("✅ 결제 취소가 완료되었습니다.");
//    }

    @Transactional
    public void completeConfirm(String paymentId) {
        // 여기서 일단 ledgerUpdated, walletUpdated가 false로 찍힘
        PaymentOrder po = paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));
        log.info("\uD83C\uDF4E po : {}", po.toString());
        PortoneSingleResponseDTO res = portOneClient.getPayment(paymentId);

        if (res == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID);
        }

        log.info("✅ [PortOne] 응답 값 : {}", res);

        if (!"Paid".equalsIgnoreCase(res.getStatus())) {
            throw new BusinessException(ErrorCode.NOT_INVALID_PAYMNET_BY_PORTONE);
        }

        Long total = (res.getAmount() != null) ? res.getAmount().getPaid() : null;

        if (!Objects.equals(po.getAmount(), total) ||
                (res.getCurrency() == null || !po.getCurrency().equalsIgnoreCase(res.getCurrency()))) {
            throw new BusinessException(ErrorCode.NOT_EQUAL_CURRENCY_OR_AMOUNT);
        }

        if (!po.isLedgerUpdated() || !po.isWalletUpdated()) {
            throw new BusinessException(ErrorCode.NOT_SETTLED_PAYMENT);
        }
        log.info("\uD83C\uDF4E 결제 확인 요청 이벤트 완료");
//
    }


    /**
     * 결제 요청에 대한 처리
     * PaymentOrder 저장이 가능
     *
     */
    @Transactional
    public void handlePaymentRequested(PaymentRequestDTO paymentRequestDTO) {
        paymentRequestProducer.send(paymentRequestDTO);
    }


    /**
     * 웹훅 성공/실패 들어왔을 때 상태 반영 + 필요시 정산 이벤트 발행
     */
    @Transactional
    public void handleWebhook(PortoneWebhookDTO payload) {
//        payload에서 값 추출
        final String paymentId = payload.getPayment_id();
        final String txId = payload.getTx_id();
        final String webhookStatus = payload.getStatus();

//        status가 실패 또는 취소일 때 바로 카프카로 전송
        log.info("🥰🥰🥰 status : {}", webhookStatus);
//        paymentOrder 조회
        PaymentOrder paymentOrder =paymentOrderService.getPaymentOrderByPaymentId(paymentId);

//       POINT_CHARGE -> TOPUP
//        POINT_PAYMENT, GENERAL_PAYMENT -> PURCHASE

//        예매 측에 이벤트 발행시켜 줌.
        if (webhookStatus.equals("failed") || webhookStatus.equals("cancelled")) {
            String prefix = PaymentOrderStatusUtil.extractPrefix(paymentOrder.getPaymentOrderStatus());
            PaymentOrderStatus failStatus = PaymentOrderStatus.valueOf(prefix + "_" + webhookStatus.toUpperCase());
            paymentOrderService.changeStatus(paymentOrder, failStatus);
            paymentOrderService.updateTxIdIfAbsent(paymentOrder, txId);

            paymentStatusProducer.send(PaymentStatusDTO.builder()
                    .method("payment")
                    .reservationNumber(paymentOrder.getBookingId())
                    .success(false)
                    .build());
            return;
        }

        String prefix = PaymentOrderStatusUtil.extractPrefix(paymentOrder.getPaymentOrderStatus());
        PaymentOrderStatus updatedStatus=PaymentOrderStatus.valueOf(prefix+"_"+webhookStatus.toUpperCase(Locale.ROOT));

        if(updatedStatus==PaymentOrderStatus.POINT_PAYMENT_PAID){
            Long amount = paymentOrder.getAmount();
            Long availableBalance = tekcitPayAccountService.getTekcitPayAccountById(paymentOrder.getBuyerId()).getAvailableBalance();

            if(amount>availableBalance){
                throw new BusinessException(ErrorCode.NOT_ENOUGH_AVAILABLE_TEKCIT_PAY_POINT);
            }
        }

        log.info("updatedStatus : {}",updatedStatus);

//        PortOne webhook이 주는 상태에 따라 상태값 변경 (변경되지 않을 수 있음.)
        paymentOrderService.changeStatus(paymentOrder, updatedStatus);
//        Webhook Ready일 때 발급되는 tx_id 저장
        paymentOrderService.updateTxIdIfAbsent(paymentOrder, txId);

//        Payment Event 생성
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                paymentEventProducer.send(PaymentEventMessageDTO.fromPaymentOrder(paymentOrder));
                if (!updatedStatus.name().endsWith("PAID")) return;

                switch (updatedStatus) {
                    case GENERAL_PAYMENT_PAID: {
                        // 정산만
                        paymentStatusProducer.send(PaymentStatusDTO.builder()
                                .method("payment")
                                .reservationNumber(paymentOrder.getBookingId())
                                .success(true)
                                .build());
                    }
                    case POINT_PAYMENT_PAID: {
                        // 포인트 차감(멱등/락 보장) → 성공하면 정산
                        tekcitPayAccountService.decreaseAvailableBalance(paymentOrder.getBuyerId(), paymentOrder.getAmount());
                        paymentStatusProducer.send(PaymentStatusDTO.builder()
                                .method("payment")
                                .reservationNumber(paymentOrder.getBookingId())
                                .success(true)
                                .build());
                    }
                    case POINT_CHARGE_PAID: {
                        tekcitPayAccountService.increaseAvailableBalance(paymentOrder.getBuyerId(), paymentOrder.getAmount());
                    }
                    default:
                        paymentSettlementProducer.send(SettlementCommandDTO.fromPaymentOrder(paymentOrder));
                        break;
                }
            }
        });
    }

    //    웹훅으로 PAID 요청 받았을 때 호출되는 메서드
    @Transactional
    public void handleSettlement(SettlementCommandDTO dto) {
        final String paymentId = dto.getPaymentId();

        LedgerDTO ledgerDTO = dto.getLedgerDTO();
        WalletDTO walletDTO = dto.getWalletDTO();

        PaymentOrder order = paymentOrderRepository.findByPaymentIdForUpdate(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

        if (order.isLedgerUpdated() && order.isWalletUpdated()) return;

        boolean ledgerDone = false;
        try {
            ledgerDone = ledgerService.saveLedgerAndUpdateLedgerUpdated(ledgerDTO, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        boolean walletDone = false;
        try {
            walletDone = walletService.saveAndUpdateWallet(walletDTO, order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (ledgerDone && walletDone) {
            order.setLedgerUpdated(true);
            order.setWalletUpdated(true);
            paymentOrderRepository.save(order);
            paymentCompleteConfirmProducer.send(paymentId);
        }
        log.info("✅ Wallet & Ledger 저장 및 업데이트 완료");
    }
}
