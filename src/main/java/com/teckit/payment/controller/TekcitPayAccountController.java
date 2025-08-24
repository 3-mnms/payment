package com.teckit.payment.controller;

import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.service.TekcitPayAccountService;
import com.teckit.payment.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tekcitpay")
public class TekcitPayAccountController {

    private  final TekcitPayAccountService tekcitPayAccountService;

    @PostMapping("/create-account")
    public ResponseEntity<SuccessResponse<Void>> createTekcitPayAccount(@RequestHeader("X-User-Id") String userIdHeader){
        Long userId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.createTekcitPayAccount(userId);
        return ApiResponseUtil.success();
    }


    @GetMapping
    public ResponseEntity<SuccessResponse<TekcitPayAccountResponseDTO>> getTekcitPayAccount(@RequestHeader("X-User-Id") String userIdHeader){
        Long userId = Long.parseLong(userIdHeader);
        TekcitPayAccountResponseDTO tekcitPayAccount = tekcitPayAccountService.getTekcitPayAccountById(userId);

        return ApiResponseUtil.success(tekcitPayAccount);
    }
}
