package com.teckit.payment.util;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.enumeration.PaymentType;

import java.util.Arrays;
import java.util.Locale;

public class PaymentOrderStatusUtil {
    public static String extractPrefix(PaymentOrderStatus status) {
        if (status == null) return null;
        String[] parts = status.name().split("_");
        return String.join("_", Arrays.copyOf(parts, parts.length - 1)); // 앞의 모든 조각
    }

    public static String extractSuffix(PaymentOrderStatus status) {
        if (status == null) return null;
        String[] parts = status.name().split("_");
        return parts[parts.length - 1];
    }

    public static PaymentOrderStatus withPhase(PaymentOrderStatus baseStatus, String newPhase) {
        if (baseStatus == null || newPhase == null) return null;
        String prefix = extractPrefix(baseStatus);
        String combined = prefix + "_" + newPhase.toUpperCase(Locale.ROOT);
        return PaymentOrderStatus.valueOf(combined);
    }

    public static PaymentType inferPaymentType(PaymentOrderStatus status) {
        if (status == null) throw new IllegalArgumentException("status is null");
        String s = status.name().toUpperCase();

        if (s.startsWith("GENERAL_PAYMENT") || s.startsWith("POINT_PAYMENT")) {
            return PaymentType.PURCHASE;
        }
        if (s.startsWith("POINT_CHARGE")) {
            return PaymentType.TOPUP;
        }
        if (s.startsWith("REFUND")) {
            return PaymentType.REFUND;
        }
        if (s.startsWith("TRANSFER")) {
            return PaymentType.TRANSFER;
        }
        throw new IllegalArgumentException("Cannot infer PaymentType from: " + s);
    }
}
