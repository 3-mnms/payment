package com.teckit.payment.controller;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
//import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.entity.PaymentEvent;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.repository.PaymentOrderRepository;
import com.teckit.payment.service.PaymentEventService;
import com.teckit.payment.service.PaymentOrchestrationService;
import com.teckit.payment.service.PaymentOrderService;
import com.teckit.payment.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentEventProducer paymentEventProducer;

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentOrchestrationService paymentOrchestrationService;

    @PostMapping("/request")
    public ResponseEntity<SuccessResponse<String>> requestPayment(@RequestBody PaymentEventDTO dto) {
        paymentEventProducer.send(dto);
        return ApiResponseUtil.success();
    }

    @PostMapping("/webhook")
    public ResponseEntity<SuccessResponse<String>> webhookHandler(@RequestBody PortoneWebhookDTO payload,
                                                 @RequestHeader("webhook-id") String webhookId,
                                                 @RequestHeader("webhook-signature") String webhookSignature,
                                                 @RequestHeader("webhook-timestamp") String webhookTimestamp
    ) {
        boolean exists = paymentOrderRepository.existsByPaymentId(payload.getPayment_id());
        if (!exists) {
            throw new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID);
        }

        paymentOrchestrationService.handleWebhook(payload);
        return ApiResponseUtil.success();
    }

    @PostMapping("/complete/{paymentId}")
    public ResponseEntity<SuccessResponse<String>> completeConfirm(@PathVariable String paymentId) {
        paymentOrchestrationService.completeConfirm(paymentId);
        return ApiResponseUtil.success();
    }
}



