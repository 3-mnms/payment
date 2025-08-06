package com.teckit.payment.kafka;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.repository.PaymentEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final PaymentEventRepository paymentEventRepository;


//    @KafkaListener(topics = "${app.kafka.topic.payment-event}", groupId = "payment-group")
//    public void debugConsumer(String message) {
//        log.info("📩 Kafka 메시지 내용 확인: {}", message);
//    }
//
//    @KafkaListener(
//            topics = "${app.kafka.topic.payment-event}",
//            groupId = "payment-consumer-group",
//            containerFactory = "paymentEventKafkaListenerContainerFactory"
//    )
//
//    @Transactional
//    public void consume(PaymentEventDTO dto) {
//        // 실제 처리 로직은 여기에 작성 (예: DB 저장)
//        PaymentEvent e = PaymentEvent.builder()
//                .checkout_id(dto.getCheckout_id())
//                .buyer_id("aa1123")
//                .seller_id("aa445")
//                .amount(dto.getAmount())
//                .build();
//
//        try{
//            paymentEventRepository.save(e);
//            log.info("✅ Consumer PaymentEvent DTO 저장 완료");
//        }catch(Exception ex){
//            log.error("저장 실패 - {}",ex);
//        }
//    }
}
