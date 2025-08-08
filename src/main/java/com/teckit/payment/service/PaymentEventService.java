package com.teckit.payment.service;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentEventService {
    private final PaymentEventRepository paymentEventRepository;

    public PaymentEvent getPaymentEvent(String paymentId) {
        return paymentEventRepository.findByPaymentId(paymentId).orElseThrow();
    }
}
