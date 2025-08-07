package com.teckit.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
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
    private String seller_id;

    @NotBlank
    private String currency;

    @NotBlank
    private String payMethod;


}
