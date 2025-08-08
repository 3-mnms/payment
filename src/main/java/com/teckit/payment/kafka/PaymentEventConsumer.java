package com.teckit.payment.kafka;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.repository.PaymentEventRepository;
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
    private final PaymentEventRepository paymentEventRepository;


//    @KafkaListener(topics = "${app.kafka.topic.payment-event}", groupId = "payment-group")
//    public void debugConsumer(String message) {
//        log.info("📩 Kafka 메시지 내용 확인: {}", message);
//    }

    @KafkaListener(
            topics = "${app.kafka.topic.payment-event}",
            groupId = "payment-consumer-group",
            containerFactory = "paymentEventKafkaListenerContainerFactory"
    )

    @Transactional
    public void consume(PaymentEventDTO dto) {
        // 실제 처리 로직은 여기에 작성 (예: DB 저장)
        LocalDateTime now = LocalDateTime.now();

        PaymentEvent e = PaymentEvent.builder()
                .festivalId(dto.getFestivalId())
                .paymentId(dto.getPaymentId())
                .buyerId("aa1123")
                .sellerId(dto.getSellerId())
                .eventType(dto.getEventType())
                .currency(dto.getCurrency())
                .amount(dto.getAmount())
                .payMethod(dto.getPayMethod())
                .timestamp(now)
                .build();

        try{
            paymentEventRepository.save(e);
            log.info("✅ Consumer PaymentEvent DTO 저장 완료");
        }catch(Exception ex){
            log.error("저장 실패 - {}",ex);
        }
    }
}
