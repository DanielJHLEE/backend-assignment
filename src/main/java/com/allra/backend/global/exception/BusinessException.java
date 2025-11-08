package com.allra.backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ⚙️ BusinessException
 * - 비즈니스 로직 위반 시 발생
 *   (예: 재고 부족, 중복 요청, 처리 불가능 상태 등)
 * - HTTP 상태코드 409(CONFLICT) 반환
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
