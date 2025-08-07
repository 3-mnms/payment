package com.teckit.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//결제 이벤트가 발생했을 때 해당 이벤트를 저장하는 DB
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    @Id
    private String eId;

    private String festival_id;

    @Column(name = "payment_id",nullable = false)
    private String payment_id;

    @Column(nullable = false)
    private String buyer_id;

    @Column(nullable = false)
    private String seller_id;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String event_type; // ex: payment.requested, payment.failed

    @Column(nullable = false)
    private LocalDateTime timestamp;

}
