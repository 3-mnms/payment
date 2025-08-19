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
        PaymentEvent e = PaymentEvent.builder()
                .festivalId(paymentEventMessageDTO.getFestivalId())
                .paymentId(paymentEventMessageDTO.getPaymentId())
                .bookingId(paymentEventMessageDTO.getBookingId())
//                buyerId는 access token 이용해서
                .buyerId(paymentEventMessageDTO.getBuyerId())
                .sellerId(paymentEventMessageDTO.getSellerId())
                .eventType(paymentEventMessageDTO.getEventType())
                .currency(paymentEventMessageDTO.getCurrency())
                .amount(paymentEventMessageDTO.getAmount())
                .payMethod(paymentEventMessageDTO.getPayMethod())
                .build();

            paymentEventRepository.save(e);
            log.info("✅ Consumer PaymentEvent DTO 저장 완료");
    }
}
