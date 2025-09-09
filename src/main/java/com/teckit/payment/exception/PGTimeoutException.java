package com.teckit.payment.exception;

import lombok.Getter;

import java.util.concurrent.TimeoutException;

@Getter
public class PGTimeoutException extends TimeoutException {
    private final ErrorCode errorCode;

    public PGTimeoutException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }

    public PGTimeoutException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode=errorCode;
    }
}
