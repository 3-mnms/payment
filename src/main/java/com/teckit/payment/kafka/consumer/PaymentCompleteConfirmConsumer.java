package com.teckit.payment.kafka.consumer;

import com.teckit.payment.dto.request.SettlementCommandDTO;
import com.teckit.payment.service.PaymentOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentCompleteConfirmConsumer {
    private final PaymentOrchestrationService paymentOrchestrationService;

    @KafkaListener(
            topics = "payment-confirm-events",
            groupId = "payment-confirm-events-consumer-group"
    )
    public void consume(String paymentId){
        paymentOrchestrationService.completeConfirm(paymentId);
    }
}
