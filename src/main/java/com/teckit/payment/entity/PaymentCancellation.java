package com.teckit.payment.entity;

import com.teckit.payment.enumeration.CancellationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "payment_cancellation",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_cancel_ext_id", columnNames = "external_cancel_id"),
                @UniqueConstraint(name="uk_cancel_pg_id",  columnNames = "pg_cancel_id")
        })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id", nullable = false)
    private PaymentOrder order;

    @Column(name = "external_cancel_id")
    private String externalCancelId;

    @Column(name="pg_cancellation_id")
    private String pgCancellationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CancellationStatus status;

    private Long amount;

    @Column(name="tax_free_amount", nullable=false)
    private Long taxFreeAmount;

    @Column(name="vat_amount")
    private Long vatAmount;

    private String reason;

    @Column(name = "trigger_type")
    private String trigger; // "API" | "WEBHOOK"
    private String receiptUrl;

    @Column(name="requested_at", nullable=false)
    private Instant requestedAt;

    @Column(name="cancelled_at")
    private Instant cancelledAt;
}
