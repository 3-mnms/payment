package com.teckit.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderDTO {
    private String paymentId;
    private Long amount;
    private String currency;
    private String payMethod;
    private LocalDateTime payTime;
}
