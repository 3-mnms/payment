package com.teckit.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerDTO {
    @NotNull
    private Long buyerId;

    @NotNull
    private Long sellerId;

    @NotBlank
    private String paymentId;

    @NotBlank
    private String txId;

    @NotBlank
    private String bookingId;

    @NotNull
    private Long amount;

    @NotBlank
    private String currency;
}
