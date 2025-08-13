package com.teckit.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name="ledger")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_type")
    private String transactionType; // 예: DEBIT, CREDIT

    @Column(name="user_id")
    private Long userId;// 해당 거래의 대상

    @NotNull
    private Long amount;

    private String currency;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "tx_id")
    private String txId;

    @Column(name="booking_id")
    private String bookingId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}