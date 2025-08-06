package com.teckit.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

//결제 실행자는 결제 주문을 데이터베이스에 저장.
// webhook으로 받아와야 할 것 같음.
@Entity
public class PaymentOrder {
    @Id
    private String payment_id;

    private String tx_id;

    private String buyer_id;

    private String seller_id;

    private String amount;

    private String currency;

    private String pay_method;


//    추후 enum 으로 변경
    private String payment_order_status;

    private boolean ledger_updated;

    private boolean wallet_updated;

    private LocalDateTime last_updated_at;
}
