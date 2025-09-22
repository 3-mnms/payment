package com.teckit.payment.kafka.producer;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
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
public class PaymentStatusProducer {

    @Qualifier("paymentStatusKafkaTemplate")
    private  final KafkaTemplate<String, PaymentStatusDTO> paymentStatusKafkaTemplate;

    @Value("${app.kafka.topic.payment-status}")
    private String topic;

    public void send(PaymentStatusDTO paymentStatusDTO) {
        paymentStatusKafkaTemplate.send(topic, paymentStatusDTO.getReservationNumber(), paymentStatusDTO );
        log.info("✅ PaymentStatus 전송 완료: " + paymentStatusDTO.toString());
    }
}
