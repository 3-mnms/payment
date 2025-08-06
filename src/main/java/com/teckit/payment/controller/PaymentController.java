package com.teckit.payment.controller;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
import com.teckit.payment.kafka.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentEventProducer producer;

    @PostMapping("/event")
    public ResponseEntity<String> createEvent(@RequestBody PaymentEventDTO dto) {
        producer.send(dto);
        return ResponseEntity.ok("✅ 이벤트 전송 완료");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody PortoneWebhookDTO payload,
                                                 @RequestHeader("webhook-id") String webhookId,
                                                 @RequestHeader("webhook-signature") String webhookSignature,
                                                 @RequestHeader("webhook-timestamp") String webhookTimestamp
                                                 ){

        log.info("📥 포트원 웹훅 수신 " +
                "webhookId {}", webhookId+
                "webhook-signature {}",webhookSignature+
                "webhook-timestamp {}",webhookTimestamp);
        log.info("\uD83D\uDE2D 포트원 데이터 수신 {}",payload);

//        준비 로직
//        거절 로직
//        완료 로직


        return ResponseEntity.ok("✅ 웹훅 수신 완료");
    }

}
