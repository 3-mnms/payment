package com.teckit.payment.kafka.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompleteConfirmProducer {

    @Qualifier("paymentCompleteConfirmKafkaTemplate")
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic.payment-confirm-event}")
    private String topic;

    public void send(String paymentId) {
        kafkaTemplate.send("payment-confirm-events", paymentId);
        log.info("Payment complete confirm message sent to topic: {}", paymentId);
    }
}
