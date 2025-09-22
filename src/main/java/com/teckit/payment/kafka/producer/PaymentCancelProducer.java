package com.teckit.payment.kafka.producer;

import com.teckit.payment.dto.response.PaymentCancelEventDTO;
import com.teckit.payment.dto.response.PaymentStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCancelProducer {

    @Qualifier("paymentCancelKafkaTemplate")
    private  final KafkaTemplate<String, PaymentCancelEventDTO> paymentCancelKafkaTemplate;

    @Value("${app.kafka.topic.payment-cancel}")
    private String topic;

    public void send(PaymentCancelEventDTO paymentCancelEventDTO) {
        paymentCancelKafkaTemplate.send(topic, paymentCancelEventDTO.getReservationNumber(), paymentCancelEventDTO );
        log.info("✅ PaymentCancel 전송 완료: " + paymentCancelEventDTO.toString());
    }
}
