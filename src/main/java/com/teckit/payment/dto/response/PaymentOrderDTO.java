package com.teckit.payment.dto.response;

import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.PayMethodType;
import com.teckit.payment.util.ApiResponseUtil;
import com.teckit.payment.util.PaymentOrderStatusUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderDTO {
    private String paymentId;
    private Long amount;
    private String currency;
    private PayMethodType payMethod;
    private LocalDateTime payTime;
    private String paymentStatus;
    private String transactionType;
    private Long buyerId;

    public static PaymentOrderDTO fromPaymentOrder(PaymentOrder paymentOrder){
        return PaymentOrderDTO.builder()
                .paymentId(paymentOrder.getPaymentId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .payMethod(paymentOrder.getPayMethod())
                .payTime(paymentOrder.getLastUpdatedAt())
                .paymentStatus(PaymentOrderStatusUtil.extractSuffix(paymentOrder.getPaymentOrderStatus()))
                .build();
    }

    public static PaymentOrderDTO fromPaymentOrder(PaymentOrder paymentOrder, Long currentUserId) {
        String transactionType;

        String paymentStatus=PaymentOrderStatusUtil.extractSuffix(paymentOrder.getPaymentOrderStatus());

        if (paymentStatus.equals("CANCELLED")) {
            transactionType = "DEBIT";
        } else if (paymentOrder.getBuyerId().equals(currentUserId)) {
            transactionType = "CREDIT";
        } else if (paymentOrder.getSellerId().equals(currentUserId)) {
            transactionType = "DEBIT";
        } else {
            transactionType = "UNKNOWN";
        }

        return PaymentOrderDTO.builder()
                .paymentId(paymentOrder.getPaymentId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .payMethod(paymentOrder.getPayMethod())
                .payTime(paymentOrder.getLastUpdatedAt())
                .paymentStatus(paymentStatus)
                .transactionType(transactionType)
                .build();
    }

    public static PaymentOrderDTO adminFromPaymentOrder(PaymentOrder paymentOrder, Long currentUserId) {
        String transactionType;

        if (paymentOrder.getBuyerId().equals(currentUserId)) {
            transactionType = "CREDIT";
        } else if (paymentOrder.getSellerId().equals(currentUserId)) {
            transactionType = "DEBIT";
        } else {
            transactionType = "UNKNOWN"; // 안전 장치
        }

        return PaymentOrderDTO.builder()
                .paymentId(paymentOrder.getPaymentId())
                .amount(paymentOrder.getAmount())
                .currency(paymentOrder.getCurrency())
                .payMethod(paymentOrder.getPayMethod())
                .buyerId(paymentOrder.getBuyerId())
                .payTime(paymentOrder.getLastUpdatedAt())
                .paymentStatus(PaymentOrderStatusUtil.extractSuffix(paymentOrder.getPaymentOrderStatus()))
                .transactionType(transactionType)
                .build();
    }
}
