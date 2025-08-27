package com.teckit.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PayByTekcitPayDTO {
    @NotNull
    private Long amount;

    @NotBlank
    private String paymentId;

    @NotNull
    private Long password;
}
