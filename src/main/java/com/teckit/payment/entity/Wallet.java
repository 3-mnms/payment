package com.teckit.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 결제 트랜잭션에 대한 금융 기록
// 사용자가 판매자에게 1달러를 결제하면 사용자로부터 1달러를 인출하고
//판매자에게 1달러를 지급하는 기록을 남긴다.
//즉, 한 번의 거래에 두 번의 insert가 발생하는 셈

@Entity
@Getter
@Setter
public class Wallet {
    @Id
    private String user_id; // 판매자 또는 사용자 계정

    private Long totalReceivedAmount = 0L; // 수신 누적 총액

    private Long totalPaidAmount = 0L;     // 지불 누적 총액

    private LocalDateTime lastUpdatedAt;
}
