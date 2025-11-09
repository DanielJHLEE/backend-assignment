package com.allra.backend.domain.mockapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MockCancelDto
 * 주문 취소 Mock API용 DTO 클래스
 * 
 * <p>
 * 요청(Request)과 응답(Response)을 내부 클래스로 구분하여 관리합니다.
 * </p>
 */
public class MockApiCancelDto {

    /**
     * 주문 취소 요청 DTO
     * - orderId: 주문 번호
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockCancelRequest {
        private String orderId;
    }

    /**
     * 주문 취소 응답 DTO
     * - status: 취소 상태 (CANCELED)
     * - message: 처리 메시지
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockCancelResponse {
        private String status;
        private String message;
    }
}
