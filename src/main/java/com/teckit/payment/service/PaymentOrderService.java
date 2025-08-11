package com.teckit.payment.service;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.repository.PaymentOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderService {
    private final PaymentOrderRepository paymentOrderRepository;

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
    public PaymentOrder createIfAbsent(PaymentEventDTO dto) {
        // 빠른 경로: 이미 있으면 바로 반환
        return paymentOrderRepository.findByPaymentId(dto.getPaymentId()).orElseGet(() -> {
            try {
                PaymentOrder po = PaymentOrder.builder()
                        .paymentId(dto.getPaymentId())
                        .buyerId("aa1123")
                        .festivalId(dto.getFestivalId())// ← 가능하면 DTO에서 받기
                        .sellerId(dto.getSellerId())
                        .amount(dto.getAmount())
                        .currency(dto.getCurrency())
                        .payMethod(dto.getPayMethod())
                        .paymentOrderStatus(PaymentOrderStatus.Requested)
                        .build();                           // lastUpdatedAt은 엔티티가 자동 세팅
                return paymentOrderRepository.save(po);
            } catch (DataIntegrityViolationException e) {
                // 동시성으로 다른 트랜잭션이 먼저 만든 경우
                return paymentOrderRepository.findByPaymentId(dto.getPaymentId())
                        .orElseThrow(() -> e); // 정말 없으면 예외 그대로
            }
        });
    }


}
