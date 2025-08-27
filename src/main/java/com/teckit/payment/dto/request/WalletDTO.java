package com.teckit.payment.dto.request;

import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.entity.Wallet;
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
public class WalletDTO {
    @NotBlank
    private Long sellerId;
    @NotBlank
    private Long buyerId;
    @NotNull
    private Long amount;

    public static WalletDTO fromEntity(PaymentOrder paymentOrder) {
        return WalletDTO.builder()
                .sellerId(paymentOrder.getSellerId())
                .buyerId(paymentOrder.getBuyerId())
                .amount(paymentOrder.getAmount())
                .build();
    }
}
