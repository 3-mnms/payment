package com.teckit.payment.controller;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.dto.request.PaymentRequestDTO;
import com.teckit.payment.dto.request.PortoneWebhookDTO;
//import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.kafka.producer.PaymentEventProducer;
import com.teckit.payment.service.PaymentOrchestrationService;
import com.teckit.payment.service.PaymentOrderService;
import com.teckit.payment.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
//    이것도 나중에 분리
    private final PaymentEventProducer paymentEventProducer;
    private final PaymentOrchestrationService paymentOrchestrationService;
    private final PaymentOrderService paymentOrderService;

//    @PostMapping("/refund/{paymentId}")
//    public ResponseEntity<SuccessResponse<String>> paymentCancel(@PathVariable String paymentId,
//                                                                 @RequestHeader("X-User-Id") String userIdHeader){
//        Long userId = Long.parseLong(userIdHeader); // 또는 Long.valueOf(userIdHeader)
//
//        paymentOrchestrationService.paymentCancel(paymentId,userId);
//        return ApiResponseUtil.success();
//    }

    @GetMapping("/{festivalId}")
    public ResponseEntity<SuccessResponse<List<PaymentOrderDTO>>> getPaymentOrder(@PathVariable String festivalId,
                                                                                  @RequestHeader("Authorization") String authHeader,
                                                                                  @RequestHeader("X-User-Id") String userIdHeader
    ) {
        Long userId = Long.parseLong(userIdHeader); // 또는 Long.valueOf(userIdHeader)
        List<PaymentOrderDTO> paymentOrderList = paymentOrderService.getPaymentOrderByFestivalId(festivalId,userId);
        return ApiResponseUtil.success(paymentOrderList);
    }

    @PostMapping("/request")
    public ResponseEntity<SuccessResponse<String>> requestPayment(@RequestBody PaymentRequestDTO dto,
                                                                  @RequestHeader("X-User-Id") String userIdHeader) {

        Long userId = Long.parseLong(userIdHeader); // 또는 Long.valueOf(userIdHeader)
        dto.setBuyerId(userId);
        log.info(dto.toString());

        paymentOrchestrationService.handlePaymentRequested(dto);
        return ApiResponseUtil.success();
    }

    @PostMapping("/webhook")
    public ResponseEntity<SuccessResponse<String>> webhookHandler(@RequestBody PortoneWebhookDTO payload,
                                                                  @RequestHeader("webhook-id") String webhookId,
                                                                  @RequestHeader("webhook-signature") String webhookSignature,
                                                                  @RequestHeader("webhook-timestamp") String webhookTimestamp
    ) {
        if (!paymentOrderService.getExistByPaymentId(payload.getPayment_id())) {
            throw new BusinessException(ErrorCode.NOT_FOUND_PAYMENT_ID);
        }

        paymentOrchestrationService.handleWebhook(payload);
        return ApiResponseUtil.success();
    }

    @PostMapping("/complete/{paymentId}")
    public ResponseEntity<SuccessResponse<String>> completeConfirm(@PathVariable String paymentId) {
        log.info("👩🏻‍🦰 결제 완료 요청 발생");

        paymentOrchestrationService.completeConfirm(paymentId);
        return ApiResponseUtil.success();
    }


}



