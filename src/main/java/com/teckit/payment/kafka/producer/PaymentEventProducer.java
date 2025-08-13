package com.teckit.payment.kafka.producer;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
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
    private final KafkaTemplate<String, PaymentEventMessageDTO> paymentEventKafkaTemplate;

    @Value("${app.kafka.topic.payment-event}")
    private String topic;

    public void send(PaymentEventMessageDTO paymentEventMessageDTO) {
        paymentEventKafkaTemplate.send(topic, paymentEventMessageDTO.getPaymentId() , paymentEventMessageDTO);
        log.info("✅ PaymentEvent 전송 완료: " + paymentEventMessageDTO);
    }
}
