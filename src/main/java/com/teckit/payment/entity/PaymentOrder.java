package com.teckit.payment.entity;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//결제 실행자는 결제 주문을 데이터베이스에 저장.
// webhook으로 받아와야 할 것 같음.
@Entity
@Table(name = "payment_order",
        uniqueConstraints = @UniqueConstraint(name = "uk_payment_order_payment_id", columnNames = "paymentId"))
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PaymentOrder {
    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "tx_id", nullable = true)
    private String txId;

    @Column(name = "festival_id",nullable=false)
    private String festivalId;

    //    얘네를 어떻게 보내줄 것인지
    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "pay_method", nullable = false)
    private String payMethod;

    //    추후 enum 으로 변경
    @Column(name = "payment_order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus paymentOrderStatus=PaymentOrderStatus.Requested;

    @Column(name = "ledger_updated", nullable = false)
    private boolean ledgerUpdated=false;

    @Column(name = "wallet_updated", nullable = false)
    private boolean walletUpdated=false;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    public void prePersist() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /** update 전 실행 */
    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
