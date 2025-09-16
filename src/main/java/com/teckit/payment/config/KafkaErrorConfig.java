//package com.teckit.payment.config;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.TopicPartition;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.util.backoff.FixedBackOff;
//
//@Configuration
//@Slf4j
//public class KafkaErrorConfig {
//    @Bean
//    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<Object, Object> template) {
//        return new DeadLetterPublishingRecoverer(template, (record, ex) -> {
//            String dltTopic = record.topic() + ".DLT";
//            return new TopicPartition(dltTopic, record.partition());
//        });
//    }
//
//    @Bean
//    public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recoverer) {
//        // 1초 간격으로 최대 3회 재시도 후 실패 시 DLT
//        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
//        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
//
//        // 재시도 의미 없는 예외는 즉시 DLT
//        handler.addNotRetryableExceptions(
//                org.springframework.kafka.support.serializer.DeserializationException.class,
//                org.apache.kafka.common.errors.SerializationException.class,
//                org.apache.kafka.common.errors.RecordTooLargeException.class,
//                org.apache.kafka.common.errors.AuthorizationException.class,
//                com.teckit.payment.exception.BusinessException.class // 도메인 검증 실패는 즉시 DLT
//        );
//
//        // 재시도 로그
//        handler.setRetryListeners((record, ex, attempt) ->
//                log.warn("Retry {} for topic={}, key={}, ex={}",
//                        attempt, record.topic(), record.key(), ex.toString())
//        );
//
//        return handler;
//    }
//}
