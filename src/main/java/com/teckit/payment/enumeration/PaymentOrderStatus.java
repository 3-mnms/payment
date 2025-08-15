package com.teckit.payment.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum PaymentOrderStatus {
    Payment_Requested, Payment_Paid, Payment_Failed, Payment_Cancelled,Payment_Ready,Payment_Rejected,
    REFUND_Succeeded;

    @JsonCreator
    public static PaymentOrderStatus from(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toUpperCase(Locale.ROOT);

        switch (s) {
            case "PAYMENT_REQUESTED": return Payment_Requested;
            case "PAYMENT_PAID":      return Payment_Paid;
            case "PAYMENT_FAILED":    return Payment_Failed;
            case "PAYMENT_CANCELLED": return Payment_Cancelled;
            case "PAYMENT_READY":     return Payment_Ready;
            case "PAYMENT_REJECTED": return Payment_Rejected;
            case "REQUESTED": return Payment_Requested;
            case "PAID":      return Payment_Paid;
            case "FAILED":    return Payment_Failed;
            case "CANCELLED": return Payment_Cancelled;
            case "READY":     return Payment_Ready;
            case "REJECTED": return Payment_Rejected;

            default: throw new IllegalArgumentException("Unsupported PaymentOrderStatus: " + raw);
        }
    }
}
