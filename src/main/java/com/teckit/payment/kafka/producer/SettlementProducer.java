package com.teckit.payment.kafka.producer;

import com.teckit.payment.dto.request.SettlementCommandDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementProducer {

    @Qualifier("settlementCommandKafkaTemplate")
    private final KafkaTemplate<String, SettlementCommandDTO> kafkaTemplate;

    @Value("${app.kafka.topic.settlement-command}")
    private String topic;

    public void send(SettlementCommandDTO dto) {
        kafkaTemplate.send(topic, dto.getPaymentId(), dto);
        log.info("✅ SettlementCommand 전송 완료: {}", dto);
    }
}