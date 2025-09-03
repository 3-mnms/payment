package com.teckit.payment.service;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.enumeration.PaymentType;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.repository.PaymentOrderRepository;
import com.teckit.payment.util.PaymentOrderStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.teckit.payment.enumeration.PaymentOrderStatus.POINT_CHARGE_PAID;
import static com.teckit.payment.enumeration.PaymentOrderStatus.POINT_PAYMENT_PAID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderService {
    private final PaymentOrderRepository paymentOrderRepository;

    @Transactional(readOnly = true)
    public PaymentOrder getPaymentOrderByBookingId(String bookingId){
        return paymentOrderRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));
    }
    @Transactional(readOnly = true)
    public PaymentOrder getPaymentOrderByBookingId(String bookingId,Long userId){
        return paymentOrderRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));
    }

    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
        return paymentOrderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID));
    }

    @Transactional
    public void changeStatus(PaymentOrder order, PaymentOrderStatus status) {
        if(order.getPaymentOrderStatus().equals(status)) return;

        order.setPaymentOrderStatus(status);
        order.setLastUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void updateTxIdIfAbsent(PaymentOrder order, String txId) {
        if (txId == null || txId.isBlank()) return;
        if (order.getTxId() != null && !order.getTxId().isBlank()) return;

        order.setTxId(txId);
    }


    @Transactional
    public void findOrCreateFromEvent(PaymentEventMessageDTO paymentEventMessageDTO) {
        // 빠른 경로: 이미 있으면 바로 반환
        paymentOrderRepository.findByPaymentId(paymentEventMessageDTO.getPaymentId()).orElseGet(() -> {
            try {
                PaymentType inferredType = PaymentOrderStatusUtil.inferPaymentType(paymentEventMessageDTO.getEventType());

                PaymentOrder po = PaymentOrder.builder()
                        .paymentId(paymentEventMessageDTO.getPaymentId())
                        .bookingId(paymentEventMessageDTO.getBookingId())
                        .buyerId(paymentEventMessageDTO.getBuyerId())
                        .festivalId(paymentEventMessageDTO.getFestivalId())// ← 가능하면 DTO에서 받기
                        .sellerId(paymentEventMessageDTO.getSellerId())
                        .amount(paymentEventMessageDTO.getAmount())
                        .currency(paymentEventMessageDTO.getCurrency())
                        .payMethod(paymentEventMessageDTO.getPayMethod())
                        .paymentOrderStatus(paymentEventMessageDTO.getEventType())
                        .paymentType(inferredType)
                        .ledgerUpdated(false)
                        .walletUpdated(false)
                        .build();                           // lastUpdatedAt은 엔티티가 자동 세팅
                return paymentOrderRepository.save(po);
            } catch (DataIntegrityViolationException e) {
                // 동시성으로 다른 트랜잭션이 먼저 만든 경우
                return paymentOrderRepository.findByPaymentId(paymentEventMessageDTO.getPaymentId())
                        .orElseThrow(() -> e); // 정말 없으면 예외 그대로
            }
        });
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
                            .paymentStatus(PaymentOrderStatusUtil.extractSuffix(po.getPaymentOrderStatus()))
                            .build();
                }).toList();
    }



    public boolean getExistByPaymentId(String paymentId) {
        return paymentOrderRepository.existsByPaymentId(paymentId);
    }

    public Page<PaymentOrder> getTekcitPayHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return paymentOrderRepository
                .findByBuyerIdAndPaymentOrderStatusInAndLedgerUpdatedTrueAndWalletUpdatedTrue(
                        userId,
                        List.of(POINT_PAYMENT_PAID, POINT_CHARGE_PAID),
                        pageable
                );
    }

    @Transactional
    public void saveAll(List<PaymentOrder> paymentOrders) {
        paymentOrderRepository.saveAll(paymentOrders);
    }
}
