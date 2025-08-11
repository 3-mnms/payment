package com.teckit.payment.entity;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//결제 이벤트가 발생했을 때 해당 이벤트를 저장하는 DB
@Entity(name = "payment_event")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eId;

    @Column(name = "festival_id")
    private String festivalId;

    @Column(name = "payment_id",nullable = false)
    private String paymentId;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name="seller_id",nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private Long amount;

    @Column(name="pay_method" ,nullable = false)
    private String payMethod;

    @Column(nullable = false)
    private String currency;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus eventType; // ex: payment.requested, payment.failed

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}
