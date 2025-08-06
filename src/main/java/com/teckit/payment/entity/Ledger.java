package com.teckit.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Ledger {
    @Id
    @GeneratedValue
    private Long id;

    private String transaction_type; // 예: DEBIT, CREDIT

    private String user_id;// 해당 거래의 대상

    private String amount;

    private String currency;

    private String payment_order_id;

    private LocalDateTime createdAt;
}