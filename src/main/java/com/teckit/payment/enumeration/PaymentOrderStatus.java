package com.teckit.payment.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum PaymentOrderStatus {
    Requested, Paid, Failed, Cancelled,Ready;

    @JsonCreator
    public static PaymentOrderStatus from(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toUpperCase(Locale.ROOT);

        switch (s) {
            case "REQUESTED": return Requested;
            case "PAID":      return Paid;
            case "FAILED":    return Failed;
            case "CANCELLED": return Cancelled;
            case "READY":     return Ready;
            default: throw new IllegalArgumentException("Unsupported PaymentOrderStatus: " + raw);
        }
    }
}
