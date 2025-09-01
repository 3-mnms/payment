package com.teckit.payment.controller;

import com.teckit.payment.dto.request.CreateRequestDTO;
import com.teckit.payment.dto.request.PayByTekcitPayDTO;
import com.teckit.payment.dto.request.TransferRequestDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.service.TekcitPayAccountService;
import com.teckit.payment.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "테킷 페이 가입 API",
            description = "비밀 번호는 String"
    )
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
    @Operation(
            summary = "테킷 페이 조회 API",
            description = " "


    )
    public ResponseEntity<SuccessResponse<TekcitPayAccountResponseDTO>> getTekcitPayAccount(@RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.parseLong(userIdHeader);
        TekcitPayAccountResponseDTO tekcitPayAccount = tekcitPayAccountService.getTekcitPayAccountById(userId);

        return ApiResponseUtil.success(tekcitPayAccount);
    }

    @Operation(
            summary = "테킷 페이 결제 요청 API",
            description = "amount : 가격 , paymentId : 랜덤 발생, password : 비밀번호 (string) "
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> payByTekcitPayAccount(
            @Valid @RequestBody PayByTekcitPayDTO dto, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.payByTekcitPay(userId, dto);
        return ApiResponseUtil.success();
    }

    @GetMapping("/history")
    @Operation(
            summary = "테킷 페이 포인트 결제 내역 조회 API",
            description = "페이징 처리 돼 있음 "
    )
    public ResponseEntity<SuccessResponse<Page<PaymentOrderDTO>>> getTekcitPayHistory(@RequestHeader("X-User-Id") String userIdHeader,
                                                                     @RequestParam(defaultValue = "0") int page,   // 기본값: 0
                                                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.parseLong(userIdHeader);

        Page<PaymentOrderDTO> histories = tekcitPayAccountService.getTekcitPayHistory(userId, page, size).map(PaymentOrderDTO::fromPaymentOrder);

        return ApiResponseUtil.success(histories);
    }
    @PostMapping("/transfer")
    @Operation(
            summary = "양도 API"
    )
    public ResponseEntity<SuccessResponse<Void>> transferToAnotherPerson(
            @Valid @RequestBody TransferRequestDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader
            ){
        Long buyerId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.transferToAnotherPerson(dto,buyerId);
        return ApiResponseUtil.success();
    }
}
