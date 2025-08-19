package com.teckit.payment.kafka.consumer;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.service.PaymentOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final PaymentOrchestrationService  paymentOrchestrationService;

    @KafkaListener(
            topics = "${app.kafka.topic.payment-event}",
            groupId = "payment-consumer-group",
            containerFactory = "paymentEventKafkaListenerContainerFactory"
    )
    public void consume(PaymentEventMessageDTO paymentEventMessageDTO) {
        // 실제 처리 로직은 여기에 작성 (예: DB 저장)
        log.info("Receive PaymentEventMessageDTO: {}", paymentEventMessageDTO.getBuyerId());
        paymentOrchestrationService.handlePaymentRequested(paymentEventMessageDTO);
        log.info("✅ Payment Event 요청이 성공적으로 완료되었습니다. {}", paymentEventMessageDTO);
    }
}
