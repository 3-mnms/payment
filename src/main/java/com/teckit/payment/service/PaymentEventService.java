package com.teckit.payment.service;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventService {
    private final PaymentEventRepository paymentEventRepository;

    public PaymentEvent getPaymentEvent(String paymentId) {
        return paymentEventRepository.findByPaymentId(paymentId).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePaymentEvent(PaymentEventMessageDTO paymentEventMessageDTO) {
        PaymentEvent e = PaymentEvent.fromPaymentEventMessageDTO(paymentEventMessageDTO);
        paymentEventRepository.save(e);
        log.info("✅ Consumer PaymentEvent DTO 저장 완료");
    }
}
