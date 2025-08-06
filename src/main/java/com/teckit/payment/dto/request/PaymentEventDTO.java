package com.teckit.payment.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Getter
public class PaymentEventDTO {
    private String payment_id;
//    seller_info를 어떻게 ??????
//    seller_info는 등록자 id ?
    private String seller_id;

    private String order_name;

    private String amount;

    private String currency;

    private String pay_method;
}
