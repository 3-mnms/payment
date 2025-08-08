package com.teckit.payment.service;

import com.teckit.payment.dto.request.PortoneWebhookDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.repository.PaymentOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderService {
    private final PaymentOrderRepository paymentOrderRepository;

    @Transactional
    public void changeStatus(PaymentOrder order,String status) {
        order.setPaymentOrderStatus(status);
        order.setLastUpdatedAt(LocalDateTime.now());
        paymentOrderRepository.save(order);
    }

    public PaymentOrder getPaymentOrder(String paymentOrderId) {
        return paymentOrderRepository.findByPaymentId(paymentOrderId).orElseThrow();
    }

    public boolean getExistOfPaymentOrder(String paymentId) {
        return paymentOrderRepository.existsByPaymentId(paymentId);
    }

    @Transactional
    public void savePaymentOrder(PaymentEvent paymentEvent, PortoneWebhookDTO portoneWebhookDTO) {
        LocalDateTime now = LocalDateTime.now();

        PaymentOrder paymentOrder = PaymentOrder.builder()
                .paymentId(portoneWebhookDTO.getPayment_id())
                .txId(portoneWebhookDTO.getTx_id())
                .buyerId(paymentEvent.getBuyerId())
                .sellerId(paymentEvent.getSellerId())
                .amount(paymentEvent.getAmount())
                .currency(paymentEvent.getCurrency())
                .payMethod(paymentEvent.getPayMethod())
                .paymentOrderStatus(portoneWebhookDTO.getStatus())
                .lastUpdatedAt(now)
                .build();

        paymentOrderRepository.save(paymentOrder);
        log.info("Payment order has been saved successfully");
    }
}
