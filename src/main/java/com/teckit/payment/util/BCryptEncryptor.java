package com.teckit.payment.util;

import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
public class BCryptEncryptor {
    public static String encrypt(String origin){
        return BCrypt.hashpw(origin, BCrypt.gensalt());
    }

    public static void isMatch(String origin, String hashed) {
        boolean matches = BCrypt.checkpw(origin, hashed);
        if (!matches) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
