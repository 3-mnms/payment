package com.teckit.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransferRequestDTO {
    @NotNull
    private Long sellerId;

    @NotBlank
    private String paymentId;

    @NotBlank
    private String bookingId;

    @NotNull
    private Long totalAmount;

//    수수료 가격
    @NotNull
    private Long commission;
}
