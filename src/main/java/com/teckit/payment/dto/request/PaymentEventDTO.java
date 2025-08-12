package com.teckit.payment.dto.request;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private PaymentOrderStatus eventType;

    @NotNull
    private Long amount;

    @NotBlank
    private Long sellerId;

    @NotBlank
    private String currency;

    @NotBlank
    private String payMethod;


}
