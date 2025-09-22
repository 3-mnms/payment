package com.teckit.payment.dto.request;

import com.teckit.payment.entity.Ledger;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.LedgerTransactionStatus;
import com.teckit.payment.enumeration.PaymentType;
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

    @NotNull
    private PaymentType paymentType;

    public static LedgerDTO fromEntity(PaymentOrder paymentOrder) {
        return LedgerDTO.builder()
                .buyerId(paymentOrder.getBuyerId())
                .sellerId(paymentOrder.getSellerId())
                .paymentId(paymentOrder.getPaymentId())
                .txId(paymentOrder.getTxId())
                .bookingId(paymentOrder.getBookingId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .paymentType(paymentOrder.getPaymentType())
                .build();
    }

}
