package com.teckit.payment.kafka.producer;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PaymentEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    @Qualifier("paymentEventKafkaTemplate")
    private final KafkaTemplate<String, PaymentEventMessage> paymentEventKafkaTemplate;

    @Value("${app.kafka.topic.payment-event}")
    private String topic;

    public void send(PaymentEventMessage paymentEventMessage) {
        paymentEventKafkaTemplate.send(topic, paymentEventMessage.getPaymentEventDTO().getPaymentId() ,paymentEventMessage);
        log.info("✅ PaymentEvent 전송 완료: " + paymentEventMessage);
    }
}
