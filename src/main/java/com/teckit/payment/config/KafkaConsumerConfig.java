package com.teckit.payment.config;

import com.teckit.payment.dto.request.PaymentEventDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaAddress;

//    ConsumerFactory는 Kafka로부터 데이터를 받아오는 Consumer 인스턴스를 생성하는 역할
//    PaymentEvnetDTO 타입의 메시지를 소비하는 Consumer를 위한 팩토리
    @Bean
    public ConsumerFactory<String, PaymentEventDTO> paymentEventConsumerFactory() {
//        json 메시지 -> PaymentEventDTO 객체로 역직렬화하는 설정
        JsonDeserializer<PaymentEventDTO> deserializer = new JsonDeserializer<>(PaymentEventDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-group");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

//    여러 개의 Consumer Thread가 병렬로 메시지를 처리할 수 있게 하기 위함.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEventDTO> paymentEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentEventConsumerFactory());
        return factory;
    }

    // 🔹 RefundEvent Consumer 설정
//    @Bean
//    public ConsumerFactory<String, RefundEventDTO> refundEventConsumerFactory() {
//        JsonDeserializer<RefundEventDTO> deserializer = new JsonDeserializer<>(RefundEventDTO.class);
//        deserializer.setRemoveTypeHeaders(false);
//        deserializer.addTrustedPackages("*");
//        deserializer.setUseTypeMapperForKey(true);
//
//        Map<String, Object> config = new HashMap<>();
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, "refund-group");
//
//        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, RefundEventDTO> refundEventKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, RefundEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(refundEventConsumerFactory());
//        return factory;
//    }
}
