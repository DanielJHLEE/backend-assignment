package com.allra.backend.domain.payment.exception;

import lombok.Getter;

/**
 * 결제 도메인 전용 예외 클래스
 * - PaymentErrorCode Enum 기반으로 생성
 */
@Getter
public class PaymentException extends RuntimeException {

    private final PaymentErrorCode errorCode;

    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
