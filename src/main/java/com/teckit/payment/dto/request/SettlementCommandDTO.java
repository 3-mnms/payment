package com.teckit.payment.dto.request;

import com.teckit.payment.entity.Ledger;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.LedgerScope;
import com.teckit.payment.enumeration.PaymentType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementCommandDTO {
    @NotBlank
    private String paymentId;
    @NotBlank
    private LedgerDTO ledgerDTO;
    @NotBlank
    private WalletDTO walletDTO;

    public static SettlementCommandDTO fromPaymentOrder(PaymentOrder paymentOrder) {
        return SettlementCommandDTO.builder()
                .walletDTO(WalletDTO.fromEntity(paymentOrder))
                .ledgerDTO(LedgerDTO.fromEntity(paymentOrder))
                .paymentId(paymentOrder.getPaymentId())
                .build();
    }
}