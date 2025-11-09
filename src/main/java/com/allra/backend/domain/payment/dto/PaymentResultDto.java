package com.allra.backend.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentDto
 * 
 * MockApi 응답을 내부 표준 형태로 변환해 전달하는 DTO 클래스.
 * 외부 Mock API 구조와 유사하지만, 시스템 내부에서만 사용한다.
 */
public class PaymentResultDto {

    /**
     * 주문 생성 응답 DTO
     * (MockApiOrderDto.MockOrderCreateResponseDto와 구조 동일)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderCreateResponse {
        private String orderId; // ORD_yyyymmdd_HHmmss_랜덤
        private String status;  // CREATED
        private String message; // 생성 결과 메시지
    }

    /**
     * 결제 응답 DTO
     * (MockApiPaymentDto.MockPayResponse와 구조 동일)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderResultResponse {
        private String status;        // SUCCESS / FAILED
        private String transactionId; // txn_xxxx
        private String message;       // 결제 결과 메시지
    }

    /**
     * 주문 취소 응답 DTO
     * (MockApiCancelDto.MockCancelResponse와 구조 동일)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderCancelResponse {
        private String status;  // CANCELED
        private String message; // 취소 결과 메시지
    }
}
