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
    public NewTopic PaymentSettlementCommandTopic(){
        return TopicBuilder.name("payment-settlement-commands")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic PaymentStatusTopic(){
        return TopicBuilder.name("payment-status-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
