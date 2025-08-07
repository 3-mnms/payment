package com.teckit.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDTO {
//    공연 이름
    @NotBlank
    private String paymentId;
    @NotBlank
    private String festivalId;

    @NotBlank
    private String eventType;
    @NotBlank
    private String amount;

    @NotBlank
    private String sellerId;

    @NotBlank
    private String currency;

    @NotBlank
    private String payMethod;


}
