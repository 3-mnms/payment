package com.teckit.payment.util;

import com.teckit.payment.exception.global.SuccessResponse;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {
    public static <T> ResponseEntity<SuccessResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(new SuccessResponse<>(true, data, message));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data) {
        return ResponseEntity.ok(new SuccessResponse<>(true, data, "요청이 성공적으로 처리되었습니다."));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success() {
        return ResponseEntity.ok(new SuccessResponse<>(true, null, "요청이 성공적으로 처리되었습니다."));
    }
}
