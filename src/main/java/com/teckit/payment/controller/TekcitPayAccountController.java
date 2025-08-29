package com.teckit.payment.controller;

import com.teckit.payment.dto.request.CreateRequestDTO;
import com.teckit.payment.dto.request.PayByTekcitPayDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.service.TekcitPayAccountService;
import com.teckit.payment.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tekcitpay")
@Slf4j
public class TekcitPayAccountController {

    private final TekcitPayAccountService tekcitPayAccountService;

    @PostMapping("/create-account")
    public ResponseEntity<SuccessResponse<Void>> createTekcitPayAccount(
            @RequestBody CreateRequestDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader) {
        log.info("userIdHeader : {}",userIdHeader);
        Long userId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.createTekcitPayAccount(userId, dto.getPassword());
        return ApiResponseUtil.success();
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<TekcitPayAccountResponseDTO>> getTekcitPayAccount(@RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.parseLong(userIdHeader);
        TekcitPayAccountResponseDTO tekcitPayAccount = tekcitPayAccountService.getTekcitPayAccountById(userId);

        return ApiResponseUtil.success(tekcitPayAccount);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> payByTekcitPayAccount(
            @Valid @RequestBody PayByTekcitPayDTO dto, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.payByTekcitPay(userId, dto);
        return ApiResponseUtil.success();
    }

    @GetMapping("/history")
    public ResponseEntity<SuccessResponse<Page<PaymentOrderDTO>>> getTekcitPayHistory(@RequestHeader("X-User-Id") String userIdHeader,
                                                                     @RequestParam(defaultValue = "0") int page,   // 기본값: 0
                                                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.parseLong(userIdHeader);

        Page<PaymentOrderDTO> histories = tekcitPayAccountService.getTekcitPayHistory(userId, page, size).map(PaymentOrderDTO::fromPaymentOrder);

        return ApiResponseUtil.success(histories);
    }
}
