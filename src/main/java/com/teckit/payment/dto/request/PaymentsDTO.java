package com.teckit.payment.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PaymentsDTO {
//    buyer_info는 jwt 토큰 이용해서 직접 판단
    private String checkout_id;
    private List<String> credit_card_info;

}
