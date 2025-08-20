package com.teckit.payment.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentStatusDTO {
    private String reservationNumber;
    private boolean success;
}
