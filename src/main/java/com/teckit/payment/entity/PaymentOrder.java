package com.teckit.payment.entity;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.enumeration.PaymentType;
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
    @Column(name = "payment_id",nullable = false)
    private String paymentId;

    @Column(name="booking_id",nullable = false)
    private String bookingId;

    @Column(name = "tx_id")
    private String txId;


    @Column(name = "festival_id",nullable=false)
    private String festivalId;

    //    얘네를 어떻게 보내줄 것인지
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "pay_method", nullable = false)
    private String payMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    //    추후 enum 으로 변경
    @Column(name = "payment_order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus paymentOrderStatus=PaymentOrderStatus.Payment_Requested;

    @Column(name = "ledger_updated", nullable = false,columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean ledgerUpdated;

    @Column(name = "wallet_updated", nullable = false,columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean walletUpdated;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @OneToOne(mappedBy = "order",fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("requestedAt ASC")
    private PaymentCancellation paymentCancellation;

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
