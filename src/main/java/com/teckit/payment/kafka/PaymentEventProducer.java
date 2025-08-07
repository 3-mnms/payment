package com.teckit.payment.kafka;

import com.teckit.payment.dto.request.PaymentEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {
    @Qualifier("paymentEventKafkaTemplate")
    private final KafkaTemplate<String, PaymentEventDTO> kafkaTemplate;

    @Value("${app.kafka.topic.payment-event}")
    private String topic;

    public void send(PaymentEventDTO dto) {
        kafkaTemplate.send(topic, dto.getPaymentId(),dto);
        System.out.println("✅ PaymentEvent 전송 완료: " + dto);
    }
}
