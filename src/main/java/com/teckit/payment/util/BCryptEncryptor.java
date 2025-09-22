package com.teckit.payment.util;

import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class BCryptEncryptor {
    public static String encrypt(String origin){
        return BCrypt.hashpw(origin, BCrypt.gensalt());
    }

    public static void isMatch(String origin, String hashed) {
        boolean matches = BCrypt.checkpw(origin, hashed);
        if (!matches) {
            log.info("비밀번호가 틀렸습니다.");
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        log.info("비밀번호가 일치합니다");
    }
}
