package com.teckit.payment.config;


import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.dto.request.PaymentRequestDTO;
import com.teckit.payment.dto.request.SettlementCommandDTO;
import com.teckit.payment.dto.response.PaymentCancelEventDTO;
import com.teckit.payment.dto.response.PaymentStatusDTO;
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
    public KafkaTemplate<String, PaymentEventMessageDTO> paymentEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(PaymentEventMessageDTO.class));
    }

    @Bean
    public KafkaTemplate<String, SettlementCommandDTO> paymentSettlementCommandKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(SettlementCommandDTO.class));
    }

    @Bean
    public KafkaTemplate<String, String> paymentCompleteConfirmKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(String.class));
    }

    @Bean
    public KafkaTemplate<String, PaymentStatusDTO> paymentStatusKafkaTemplate() {
        return new  KafkaTemplate<>(producerFactory(PaymentStatusDTO.class));
    }

    @Bean
    public KafkaTemplate<String, PaymentCancelEventDTO> paymentCancelKafkaTemplate() {
        return new  KafkaTemplate<>(producerFactory(PaymentCancelEventDTO.class));
    }

    @Bean
    public KafkaTemplate<String, PaymentRequestDTO> paymentRequestKafkaTemplate() {
        return new  KafkaTemplate<>(producerFactory(PaymentRequestDTO.class));
    }
}