package com.teckit.payment.controller;

import com.teckit.payment.dto.api.TekcitPayAccountApiSpecification;
import com.teckit.payment.dto.request.CreateRequestDTO;
import com.teckit.payment.dto.request.PayByTekcitPayDTO;
import com.teckit.payment.dto.request.TransferRequestDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
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
public class TekcitPayAccountController implements TekcitPayAccountApiSpecification {

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
        log.info("userIdHeader : {}",userIdHeader);

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

        Page<PaymentOrderDTO> histories = tekcitPayAccountService.getTekcitPayHistory(userId, page, size)
                .map(po->PaymentOrderDTO.fromPaymentOrder(po,userId));

        return ApiResponseUtil.success(histories);
    }
    @PostMapping("/transfer")
    public ResponseEntity<SuccessResponse<Void>> transferToAnotherPerson(
            @Valid @RequestBody TransferRequestDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader
            ){
        Long buyerId = Long.parseLong(userIdHeader);
        tekcitPayAccountService.transferToAnotherPerson(dto,buyerId);
        return ApiResponseUtil.success();
    }

    @GetMapping("/admin/total-amount")
    public ResponseEntity<SuccessResponse<TekcitPayAccountResponseDTO>> getAdminTekcitPayAccountTotalAmount(@RequestHeader("X-User-Id") String userIdHeader,@RequestHeader("X-User-Role") String userRole) {
        if(!userRole.equals("ADMIN")) throw new BusinessException(ErrorCode.INVALID_USER_ROLE);

        TekcitPayAccountResponseDTO tekcitPayAccountById = tekcitPayAccountService.getTekcitPayAccountById(1L);

        return ApiResponseUtil.success(tekcitPayAccountById);
    }

    @GetMapping("/admin/history")
    public ResponseEntity<SuccessResponse<Page<PaymentOrderDTO>>> getAdminTekcitPayHistory(@RequestHeader("X-User-Role") String userRole,@RequestParam(defaultValue = "0") int page,   // 기본값: 0
                                                                          @RequestParam(defaultValue = "10") int size) {
        if(!userRole.equals("ADMIN")) throw new BusinessException(ErrorCode.INVALID_USER_ROLE);
        Page<PaymentOrderDTO> tekcitPayHistory = tekcitPayAccountService.getTekcitPayHistory(1L, page, size).map(po->PaymentOrderDTO.adminFromPaymentOrder(po,1L));
        return ApiResponseUtil.success(tekcitPayHistory);
    }
}
