package com.allra.backend.domain.product.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProductDto
 * 상품 관련 DTO 클래스
 */
public class ProductDto {

    // 상품 List/Detail 응답용DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductResponseDto {

        private Long id;
        private String name;
        private String brand;
        private String category;
        private int price;
        private int stock;
        private boolean soldOut;
        private LocalDateTime createdAt;

        // Entity -> DTO 변환 메서드
        public static ProductResponseDto fromEntity(com.allra.backend.domain.product.entity.ProductEntity product) {
            return ProductResponseDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .brand(product.getBrand())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .soldOut(product.getSoldOut())
                    .createdAt(product.getCreatedAt())
                    .build();
        }
    }   
}
