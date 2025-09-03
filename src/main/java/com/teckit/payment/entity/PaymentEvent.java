package com.teckit.payment.entity;

import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.enumeration.PayMethodType;
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
    @Column(name="e_id")
    private Long eId;

    @Column(name = "payment_id",nullable = false)
    private String paymentId;

    @Column(name="booking_id")
    private String bookingId;

    @Column(name = "festival_id")
    private String festivalId;


    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name="seller_id",nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name="pay_method" ,nullable = false)
    private PayMethodType payMethod;

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

    public static PaymentEvent fromPaymentEventMessageDTO(PaymentEventMessageDTO dto) {
        return PaymentEvent.builder()
                .festivalId(dto.getFestivalId())
                .paymentId(dto.getPaymentId())
                .bookingId(dto.getBookingId())
//                buyerId는 access token 이용해서
                .buyerId(dto.getBuyerId())
                .sellerId(dto.getSellerId())
                .eventType(dto.getEventType())
                .currency(dto.getCurrency())
                .amount(dto.getAmount())
                .payMethod(dto.getPayMethod())
                .build();

    }
}
