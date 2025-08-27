package com.teckit.payment.controller;

import com.teckit.payment.dto.request.PayByTekcitPayDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.service.TekcitPayAccountService;
import com.teckit.payment.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tekcitpay")
public class TekcitPayAccountController {

    private final TekcitPayAccountService tekcitPayAccountService;

    @PostMapping("/create-account")
    public ResponseEntity<SuccessResponse<Void>> createTekcitPayAccount(
            @RequestBody Long password,
            @RequestHeader("X-User-Id") String userIdHeader)
    {
        Long userId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.createTekcitPayAccount(userId,password);
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
        tekcitPayAccountService.payByTekcitPay(userId,dto);
        return ApiResponseUtil.success();
    }
}
