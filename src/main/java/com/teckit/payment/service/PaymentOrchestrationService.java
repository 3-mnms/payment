package com.teckit.payment.service;

import com.teckit.payment.config.PortOneClient;
import com.teckit.payment.dto.request.*;
import com.teckit.payment.dto.response.PaymentCancelDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
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

import java.util.List;
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
    //    service
    private final PaymentOrderService paymentOrderService; // 상태 변경 로직 분리 시 사용
    private final PaymentEventService paymentEventService;


    //    event
    PaymentEventProducer paymentEventProducer;
    SettlementProducer settlementProducer;

//    RestClient

    PortOneClient portOneClient;
    private final EntityManager em;

    public void paymentCancel(String paymentId, Long userId) {
//        1. paymentOrder에서 paymentId에 해당하는 entity가 존재하는지 확인
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

//        2. 결제 단건 조회 후 포트원 쪽이랑 Paid 됐는지 교차확인
        PortoneSingleResponseDTO res = portOneClient.getPayment(paymentId);

        if (!paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.Paid) || !res.getStatus().equals("PAID")
        ) {
            throw new BusinessException(ErrorCode.NOT_PAID_ORDER);
        }

//        4. 환불 준비 완료 이벤트 저장
        PaymentEventMessageDTO PaymentEventMessageDTO = PaymentEventMessageDTO.builder()
                .paymentId(paymentId)
                .festivalId(paymentOrder.getFestivalId())
                .eventType(PaymentOrderStatus.Ready)
                .amount(paymentOrder.getAmount())
                .sellerId(paymentOrder.getSellerId())
                .currency(paymentOrder.getCurrency())
                .payMethod(paymentOrder.getPayMethod())
                .build();

        PaymentEventMessageDTO paymentEventMessageDTO = PaymentEventMessageDTO.builder()
                .PaymentEventMessageDTO(PaymentEventMessageDTO)
                .userId(userId)
                .build();

        paymentEventProducer.send(paymentEventMessageDTO);

//       5. 환불 요청
        PaymentCancelDTO cancelRes = portOneClient.cancelPayment(paymentId);

//        wallet update

//        ledger update

//        paymentCancellation 엔티티 생성 -> kafka로 해야 되나 ?


//        payment cancel kafka 요청 ?
//        cancelRes.get

    }



    @Transactional(readOnly = true)
    public List<PaymentOrderDTO> getPaymentOrderByFestivalId(String festivalId, Long buyerId) {
        return paymentOrderRepository.findByFestivalIdAndBuyerIdAndLedgerUpdatedTrueAndWalletUpdatedTrue(festivalId, buyerId)
                .stream().map((po) -> {
                    return PaymentOrderDTO.builder()
                            .paymentId(po.getPaymentId())
                            .amount(po.getAmount())
                            .currency(po.getCurrency())
                            .payMethod(po.getPayMethod())
                            .payTime(po.getLastUpdatedAt())
                            .build();
                }).toList();
    }

    public boolean getExistByPaymentId(String paymentId) {
        return paymentOrderRepository.existsByPaymentId(paymentId);
    }

    @Transactional
    public void completeConfirm(String paymentId) {
        PaymentOrder po = paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

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

        em.refresh(po);

        log.info("");
        if (!po.isLedgerUpdated() || !po.isWalletUpdated()) {
            throw new BusinessException(ErrorCode.NOT_SETTLED_PAYMENT);
        }
    }


    /**
     * 결제 요청에 대한 처리
     * PaymentOrder 저장이 가능
     * */
    @Transactional
    public void handlePaymentRequested(PaymentEventMessageDTO paymentEventMessageDTO) {
//        sellerId와 buyerId, 즉 자신에게 양도 혹은 자신의 상품을 구매하는 것은 block
        if (isBuyerIdEqualsSellerId(paymentEventMessageDTO)) {
            paymentEventMessageDTO.setEventType(PaymentOrderStatus.Rejected);
            savePaymentEvent(paymentEventMessageDTO);
            return;
        }
        savePaymentEvent(paymentEventMessageDTO);
//        PaymentOrder 초기 생성
//        txId는 webhook이 Ready일 때 저장
        paymentOrderRepository.findByPaymentId(paymentEventMessageDTO.getPaymentId())
                .orElseGet(() -> paymentOrderService.createIfAbsent(paymentEventMessageDTO));
    }

//    PaymentEvent 저장
    @Transactional
    public void savePaymentEvent(PaymentEventMessageDTO paymentEventMessageDTO) {
            paymentEventService.savePaymentEvent(paymentEventMessageDTO);
    }

    private static boolean isBuyerIdEqualsSellerId(PaymentEventMessageDTO paymentEventMessageDTO) {
        return Objects.equals(paymentEventMessageDTO.getSellerId(), paymentEventMessageDTO.getBuyerId());
    }


    /**
     * 웹훅 성공/실패 들어왔을 때 상태 반영 + 필요 시 정산 이벤트 발행
     */
    @Transactional
    public void handleWebhook(PortoneWebhookDTO payload) {
//        payload에서 값 추출
        final String paymentId = payload.getPayment_id();
        final String txId = payload.getTx_id();
        final PaymentOrderStatus status = payload.getStatus();

//        paymentOrder 조회
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

//        PortOne webhook이 주는 상태에 따라 상태값 변경 (변경되지 않을 수 있음.)
        paymentOrderService.changeStatus(paymentOrder, status);
//        Webhook Ready일 때 발급되는 tx_id 저장
        paymentOrderService.updateTxIdIfAbsent(paymentOrder, txId);

//        Payment Event 생성
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
//                PaymentEvent 추가 발행
//                동기 처리로 할지 비동기 처리로 할지 타당한 근거를 대야 할 듯 ?
                PaymentEventMessageDTO dto=createPaymentEventMessageDTO(paymentOrder);
                paymentEventProducer.send(dto);

//            결제 완료 됐을 때, Legder, Wallet 갱신 이벤트 발행
                if (PaymentOrderStatus.Paid.equals(paymentOrder.getPaymentOrderStatus())) {
                    settlementProducer.send(buildSettlementCmd(paymentOrder));
                }
            }
        });
    }

    private PaymentEventMessageDTO createPaymentEventMessageDTO(PaymentOrder paymentOrder){
        return PaymentEventMessageDTO.builder()
                .paymentId(paymentOrder.getPaymentId())
                .bookingId(paymentOrder.getBookingId())
                .festivalId(paymentOrder.getFestivalId())
                .eventType(paymentOrder.getPaymentOrderStatus())
                .amount(paymentOrder.getAmount())
                .sellerId(paymentOrder.getSellerId())
                .currency(paymentOrder.getCurrency())
                .payMethod(paymentOrder.getPayMethod())
                .build();
    }

    private SettlementCommandDTO buildSettlementCmd(PaymentOrder paymentOrder) {
        return SettlementCommandDTO.builder()
                .walletDTO(createWalletDTO(paymentOrder))
                .ledgerDTO(createLedgerDTO(paymentOrder))
                .paymentId(paymentOrder.getPaymentId())
                .build();
    }
    private WalletDTO createWalletDTO(PaymentOrder paymentOrder){
        return WalletDTO.builder()
                .sellerId(paymentOrder.getSellerId())
                .buyerId(paymentOrder.getBuyerId())
                .amount(paymentOrder.getAmount())
                .build();
    }

    private LedgerDTO createLedgerDTO(PaymentOrder paymentOrder){
        return LedgerDTO.builder()
                .buyerId(paymentOrder.getBuyerId())
                .sellerId(paymentOrder.getSellerId())
                .paymentId(paymentOrder.getPaymentId())
                .txId(paymentOrder.getTxId())
                .bookingId(paymentOrder.getBookingId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .build();
    }


    @Transactional
    public void handleSettlement(SettlementCommandDTO dto) {
        final String paymentId = dto.getPaymentId();
        LedgerDTO ledgerDTO=dto.getLedgerDTO();
        WalletDTO  walletDTO=dto.getWalletDTO();

        PaymentOrder order = paymentOrderRepository.findByPaymentIdForUpdate(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));

        if (order.isLedgerUpdated() && order.isWalletUpdated()) return;

        saveLedger(ledgerDTO, order);
//        둘 다 있을 때는 ㄱㅊ


//        PaymentOrder ledger_updated true로 바꿔야 됨

        Wallet buyer = walletRepository.findById(walletDTO.getBuyerId())
                .orElseGet(() -> new Wallet(walletDTO.getBuyerId()));
        Wallet seller = walletRepository.findById(walletDTO.getSellerId())
                .orElseGet(() -> new Wallet(walletDTO.getSellerId()));

        buyer.setTotalPaidAmount(nz(buyer.getTotalPaidAmount()) + walletDTO.getAmount());
        seller.setTotalReceivedAmount(nz(seller.getTotalReceivedAmount()) + walletDTO.getAmount());

        walletRepository.save(buyer);
        walletRepository.save(seller);

        order.setWalletUpdated(true);

//        여기서 카프카로 결제 완료 요청 보내주면 될 듯 ?
    }

    private void updateLedgerAndWallet(){}

    private void saveLedger(LedgerDTO ledgerDTO, PaymentOrder order) {


        try {
            ledgerRepository.saveAll(List.of(
                    Ledger.builder().transactionType("DEBIT").userId(ledgerDTO.getBuyerId())
                            .amount(ledgerDTO.getAmount()).currency(ledgerDTO.getCurrency()).paymentId(ledgerDTO.getPaymentId()).txId(ledgerDTO.getTxId()).build(),
                    Ledger.builder().transactionType("CREDIT").userId(ledgerDTO.getSellerId())
                            .amount(ledgerDTO.getAmount()).currency(ledgerDTO.getCurrency()).paymentId(ledgerDTO.getPaymentId()).txId(ledgerDTO.getTxId()).build()
            ));
            order.setLedgerUpdated(true);
        } catch (DataIntegrityViolationException e) {
            // 이미 존재 → 멱등 처리로 간주
            order.setLedgerUpdated(true);
        }
    }

    private long nz(Long v) {
        return v == null ? 0L : v;
    }

}
