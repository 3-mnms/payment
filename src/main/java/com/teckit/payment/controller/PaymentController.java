package com.teckit.payment.controller;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.dto.request.PaymentEventMessage;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
//import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.dto.response.PaymentOrderDTO;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
//    이것도 나중에 분리
    private final PaymentEventProducer paymentEventProducer;
    private final PaymentOrchestrationService paymentOrchestrationService;

    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<SuccessResponse<String>> paymentCancel(@PathVariable String paymentId,
                                                                 @RequestHeader("X-User-Id") String userIdHeader){
        Long userId = Long.parseLong(userIdHeader); // 또는 Long.valueOf(userIdHeader)

        paymentOrchestrationService.paymentCancel(paymentId,userId);
        return ApiResponseUtil.success();
    }

    @GetMapping("/{festivalId}")
    public ResponseEntity<SuccessResponse<List<PaymentOrderDTO>>> getPaymentOrder(@PathVariable String festivalId,
                                                                                  @RequestHeader("Authorization") String authHeader,
                                                                                  @RequestHeader("X-User-Id") String userIdHeader
    ) {
        Long userId = Long.parseLong(userIdHeader); // 또는 Long.valueOf(userIdHeader)
        List<PaymentOrderDTO> paymentOrderList = paymentOrchestrationService.getPaymentOrderByFestivalId(festivalId,userId);
        return ApiResponseUtil.success(paymentOrderList);
    }

    @PostMapping("/request")
    public ResponseEntity<SuccessResponse<String>> requestPayment(@RequestBody PaymentEventDTO dto,
                                                                  @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.parseLong(userIdHeader); // 또는 Long.valueOf(userIdHeader)
        PaymentEventMessage paymentEventMessage = PaymentEventMessage.builder()
                .paymentEventDTO(dto)
                .userId(userId)
                .build();

        paymentEventProducer.send(paymentEventMessage);
        return ApiResponseUtil.success();
    }

    @PostMapping("/webhook")
    public ResponseEntity<SuccessResponse<String>> webhookHandler(@RequestBody PortoneWebhookDTO payload,
                                                                  @RequestHeader("webhook-id") String webhookId,
                                                                  @RequestHeader("webhook-signature") String webhookSignature,
                                                                  @RequestHeader("webhook-timestamp") String webhookTimestamp
    ) {
        if (!paymentOrchestrationService.getExistByPaymentId(payload.getPayment_id())) {
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



