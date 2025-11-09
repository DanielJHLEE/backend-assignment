package com.allra.backend.domain.mockapi.dto;

import java.util.List;

import com.allra.backend.domain.product.entity.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MockOrderDto
 * 주문 생성 Mock API용 DTO 클래스
 * 
 * <p>
 * 요청(Request)과 응답(Response)을 하나의 파일 내에서
 * 내부 정적(static) 클래스로 구분하여 관리합니다.
 * </p>
 */
public class MockApiOrderDto {

    /**
     * 주문 생성 요청 DTO
     * - userId: 사용자 ID
     * - productId: 상품 ID
     * - amount: 상품 총액
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockOrderCreateRequestDto {
        private Long userId;
        private List<ProductEntity> products;
        private Long amount;
    }

    /**
     * 주문 생성 응답 DTO
     * - orderId: 주문 번호
     * - status: 주문 상태 (CREATED)
     * - message: 처리 메시지
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockOrderCreateResponseDto {
        private String orderId;
        private String status;
        private String message;
    }
}
