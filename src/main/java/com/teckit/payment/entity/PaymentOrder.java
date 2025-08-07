package com.teckit.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//결제 실행자는 결제 주문을 데이터베이스에 저장.
// webhook으로 받아와야 할 것 같음.
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PaymentOrder {
    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "tx_id", nullable = false)
    private String txId;

    //    얘네를 어떻게 보내줄 것인지
    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "pay_method", nullable = false)
    private String payMethod;

    //    추후 enum 으로 변경
    @Column(name = "payment_order_status", nullable = false)
    private String paymentOrderStatus;

    @Column(name = "ledger_updated", nullable = false)
    private boolean ledgerUpdated;

    @Column(name = "wallet_updated", nullable = false)
    private boolean walletUpdated;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;
}
