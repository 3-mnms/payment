package com.teckit.payment.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name("payment-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic PaymentOrderTopic(){
        return TopicBuilder.name("payment-order-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic settlementCommandTopic(){
        return TopicBuilder.name("settlement-commands")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
