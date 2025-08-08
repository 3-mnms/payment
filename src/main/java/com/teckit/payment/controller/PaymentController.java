package com.teckit.payment.controller;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
//import com.teckit.payment.kafka.PaymentEventProducer;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.kafka.PaymentEventProducer;
import com.teckit.payment.service.PaymentEventService;
import com.teckit.payment.service.PaymentOrderService;
import com.teckit.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentEventProducer producer;
//    private final PaymentService paymentService;

    private final PaymentEventService paymentEventService;
    private final PaymentOrderService paymentOrderService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPayment(@RequestBody PaymentEventDTO dto){
        producer.send(dto);
        return ResponseEntity.ok("✅ 요청 확인");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhookHandler(@RequestBody PortoneWebhookDTO payload,
                                                 @RequestHeader("webhook-id") String webhookId,
                                                 @RequestHeader("webhook-signature") String webhookSignature,
                                                 @RequestHeader("webhook-timestamp") String webhookTimestamp
                                                 ){
        log.info("webhooktimestamp {}",webhookTimestamp);
//        PaymentOrder이 존재하지 않으면 PaymentOrder을 PaymentEvent에서 추출해서 저장
        boolean isExistPaymentOrder = paymentOrderService.getExistOfPaymentOrder(payload.getPayment_id());
//        이 부분 어떻게 해야 되냐 ^ㅣㅏㄹ
        PaymentEvent p = paymentEventService.getPaymentEvent(payload.getPayment_id());

        PaymentEventDTO paymentEventDTO=PaymentEventDTO.builder()
                .paymentId(p.getPaymentId())
                .festivalId(p.getFestivalId())
                .eventType(payload.getStatus())
                .amount(p.getAmount())
                .sellerId(p.getSellerId())
                .currency(p.getCurrency())
                .payMethod(p.getPayMethod())
                .build();

        if(!isExistPaymentOrder){
//            PaymentOrder에 저장
//            무조건 있다고 가정
            paymentOrderService.savePaymentOrder(p,payload);
        }

        if(isExistPaymentOrder){
            PaymentOrder paymentOrder = paymentOrderService.getPaymentOrder(payload.getPayment_id());
            String status=payload.getStatus();

            switch(status){
                case "Paid" -> {
                    paymentOrderService.changeStatus(paymentOrder,"Paid"); // 주문 상태를 Paid로 업데이트
                    // TODO: wallet, ledger 업데이트도 이 시점에 처리
                }
                case "Failed" -> {
                    paymentOrderService.changeStatus(paymentOrder,"Failed"); // 주문 상태를 Paid로 업데이트
                }
                case "Cancelled" -> {
                    paymentOrderService.changeStatus(paymentOrder,"Cancelled"); // 주문 상태를 Paid로 업데이트
                }
                default -> {
                    // 알 수 없는 상태
                    return ResponseEntity.badRequest().body("❌ 알 수 없는 결제 상태");
                }
            }
        }
        producer.send(paymentEventDTO);
        return ResponseEntity.ok("✅ 웹훅 수신 완료");
    }

}
