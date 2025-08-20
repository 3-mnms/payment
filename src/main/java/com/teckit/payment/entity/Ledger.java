package com.teckit.payment.entity;

import com.teckit.payment.enumeration.LedgerTransactionStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private LedgerTransactionStatus transactionType; // 예: DEBIT, CREDIT

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