package com.teckit.payment.dto.request;

import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentEventMessage {
    private Long userId;
    private PaymentEventDTO paymentEventDTO;
}
