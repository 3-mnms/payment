package com.teckit.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_FOUND_PAYMENT_ID("P001","존재하지 않는 결제 정보입니다.",HttpStatus.NOT_FOUND),
    NOT_INVALID_PAYMNET_BY_PORTONE("P002","포트원에서 결제되지 않은 Payment ID입니다.",HttpStatus.CONFLICT),
    NOT_EQUAL_CURRENCY_OR_AMOUNT("P003","금액이나 화폐 가치가 포트원 데이터와 일치하지 않습니다.",HttpStatus.CONFLICT),
    NOT_SETTLED_PAYMENT("P004","정산되지 않은 Payment ID입니다.",HttpStatus.NOT_ACCEPTABLE),
    EQUALS_SELLER_BUYER("P005","주최자는 주최측 공연을 구매할 수 없습니다.",HttpStatus.NOT_ACCEPTABLE),
    NOT_PAID_ORDER("P006","결제되지 않은 주문입니다.",HttpStatus.NOT_ACCEPTABLE)
    ;

    private final String code;        // A001, A002 등
    private final String message;     // 사용자에게 보여줄 메시지
    private final HttpStatus status;  //http status 코드

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
