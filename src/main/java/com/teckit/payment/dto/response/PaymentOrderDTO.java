package com.teckit.payment.dto.response;

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
}
