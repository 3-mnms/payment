package com.teckit.payment.config;


import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PaymentEventMessage;
import com.teckit.payment.dto.request.SettlementCommandDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;



@Configuration

public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaAddress;

    public <T> ProducerFactory<String, T> producerFactory(Class<T> clazz) {
        System.out.println(kafkaAddress);
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PaymentEventMessage> paymentEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(PaymentEventMessage.class));
    }

    @Bean
    public KafkaTemplate<String, SettlementCommandDTO> settlementCommandKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(SettlementCommandDTO.class));
    }
}