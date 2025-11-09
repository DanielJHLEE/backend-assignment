package com.allra.backend.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderDto
 * 
 * 주문 관련 요청(Request) / 응답(Response)을 통합 관리하는 DTO 클래스
 */
public class OrderDto {

    /**
     * 주문 생성 요청 DTO
     * - userId: 사용자 ID
     * - productId: 상품 ID
     * - amount: 결제 금액
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderRequestDto {
        private Long userId;
        private Long productId;
        private int amount;
    }

    /**
     * 주문 응답 DTO
     * - orderId: 주문 번호
     * - status: 주문 상태
     * - message: 처리 결과 메시지
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderResponseDto {
        private String orderId;
        private String status;
        private String message;
    }
}
