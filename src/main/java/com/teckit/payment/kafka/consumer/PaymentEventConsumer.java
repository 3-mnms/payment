package com.teckit.payment.kafka.consumer;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.repository.PaymentEventRepository;
import com.teckit.payment.service.PaymentEventService;
import com.teckit.payment.service.PaymentOrchestrationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
    public void consume(PaymentEventDTO dto) {
        // 실제 처리 로직은 여기에 작성 (예: DB 저장)
        paymentOrchestrationService.handlePaymentRequested(dto);
        log.info("✅ Payment Event 요청이 성공적으로 완료되었습니다. {}", dto);
    }
}
