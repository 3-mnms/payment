package com.teckit.payment.dto.response;

import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.PayMethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderDTO {
    private String paymentId;
    private Long amount;
    private String currency;
    private PayMethodType payMethod;
    private LocalDateTime payTime;

    public static PaymentOrderDTO fromPaymentOrder(PaymentOrder paymentOrder){
        return PaymentOrderDTO.builder()
                .paymentId(paymentOrder.getPaymentId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .payMethod(paymentOrder.getPayMethod())
                .payTime(paymentOrder.getLastUpdatedAt())
                .build();
    }
}
