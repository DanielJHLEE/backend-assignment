package com.allra.backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 🔍 NotFoundException
 * - 요청한 리소스(사용자, 상품 등)가 존재하지 않을 때 발생
 * - HTTP 상태코드 404 반환
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
