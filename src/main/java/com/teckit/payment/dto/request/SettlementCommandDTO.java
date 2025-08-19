package com.teckit.payment.dto.request;

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
}