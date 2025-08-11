package com.teckit.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementCommandDTO {
    private String paymentId;     // 키
    private Long amount;
    private String currency;
    private String sellerId;
    private String buyerId;
    private String txId;          // portone webhook에서 받은 트랜잭션 id
}