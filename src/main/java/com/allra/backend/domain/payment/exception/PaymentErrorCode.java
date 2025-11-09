package com.allra.backend.domain.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PaymentErrorCode
 * 결제 도메인에서 발생할 수 있는 오류 코드 정의
 */
@Getter
@AllArgsConstructor
public enum PaymentErrorCode {

    MOCK_API_RESPONSE_NULL("P001", "Mock API 응답이 비정상입니다."),
    MOCK_PAYMENT_FAILED("P002", "결제 요청 중 오류가 발생했습니다."),
    PAYMENT_LOG_NOT_FOUND("P003", "결제 로그를 찾을 수 없습니다."),
    CANCEL_API_FAILED("P004", "주문 취소 API 호출에 실패했습니다."),
    ORDER_NOT_FOUND("P005", "주문 정보를 찾을 수 없습니다."),
    UNKNOWN_ERROR("P999", "알 수 없는 결제 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
