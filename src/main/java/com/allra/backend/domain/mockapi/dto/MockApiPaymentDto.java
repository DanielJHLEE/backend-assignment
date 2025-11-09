package com.allra.backend.domain.mockapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MockPaymentDto
 * 결제 처리 Mock API용 DTO 클래스
 * 
 * <p>
 * 요청(Request)과 응답(Response)을 내부 클래스로 구분하여 관리합니다.
 * </p>
 */
public class MockApiPaymentDto {

    /**
     * 결제 요청 DTO
     * - orderId: 주문 번호
     * - amount: 결제 금액
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockPayRequest {
        private Long orderId;
        private int amount;
    }

    /**
     * 결제 응답 DTO
     * - status: 결제 결과 (SUCCESS / FAILED)
     * - transactionId: 트랜잭션 ID
     * - message: 처리 메시지
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockPayResponse {
        private String status;
        private String transactionId;
        private String message;
    }
}
