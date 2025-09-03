package com.teckit.payment.kafka.producer;

import com.teckit.payment.dto.request.PaymentRequestDTO;
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
public class PaymentRequestProducer {

    @Qualifier("paymentRequestKafkaTemplate")
    private  final KafkaTemplate<String, PaymentRequestDTO> paymentRequestKafkaTemplate;

    @Value("${app.kafka.topic.payment-request}")
    private String topic;

    public void send(PaymentRequestDTO dto) {
        paymentRequestKafkaTemplate.send(topic, dto.getPaymentId(), dto );
        log.info("✅ PaymentRequest 전송 완료: " + dto.toString());
    }
}
