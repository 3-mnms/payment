package com.teckit.payment.dto.request;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import lombok.Data;

@Data
public class PortoneWebhookDTO {
    private String tx_id;         // 포트원 결제 고유 ID
    private String payment_id;    // 상점 주문 번호
    private PaymentOrderStatus status;          // 결제 상태 (예: paid, failed, cancelled)
}
