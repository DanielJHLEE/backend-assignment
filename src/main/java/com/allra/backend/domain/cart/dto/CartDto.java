package com.allra.backend.domain.cart.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.product.entity.ProductEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartDto
 * 장바구니 관련 DTO 클래스
 * CartEntity 및 CartItemEntity로부터 필요한 정보를 추출하여 구성
 */
public class CartDto {

    /**
     * 사용자 장바구니(1 or N) 리스트 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCartResponseDto {
        private Long cartId; 
        private Long cartItemId;
        private Long productId;
        private String productName;
        private String brand;
        private String category;
        private Integer price;
        private Boolean soldOut;
        private Integer quantity;
        private LocalDateTime createdAt;

        /**
         * CartEntity → List<CartResponseDto> 변환
         */
        public static List<UserCartResponseDto> fromEntity(CartEntity cart) {
            return cart.getItems().stream()
                    .map(item -> {
                        ProductEntity product = item.getProduct();

                        return UserCartResponseDto.builder()
                                .cartId(cart.getId())
                                .cartItemId(item.getId())
                                .productId(product.getId())
                                .productName(product.getName())
                                .brand(product.getBrand())
                                .category(product.getCategory())
                                .price(product.getPrice())
                                .soldOut(Boolean.TRUE.equals(product.getSoldOut()))
                                .quantity(item.getQuantity())
                                .createdAt(cart.getCreatedAt())
                                .build();
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * 사용자 cartId 기준 장바구니 상세 응답 DTO (리스트)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartsIdDetailResponseDto {
        private LocalDateTime createdAt;
        private List<CartItemDetail> items;

        // 하위 아이템 DTO
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CartItemDetail {
            private Long cartItemId;
            private Long productId;
            private String productName;
            private Integer quantity;
            private Integer price;
            private Boolean soldOut;
        }

        // Entity → DTO 변환
        public static CartsIdDetailResponseDto fromEntity(CartEntity cart) {
            return CartsIdDetailResponseDto.builder()
                    .createdAt(cart.getCreatedAt())
                    .items(cart.getItems().stream()
                            .map(item -> {
                                ProductEntity product = item.getProduct();
                                return CartItemDetail.builder()
                                        .cartItemId(item.getId())
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .quantity(item.getQuantity())
                                        .price(product.getPrice())
                                        .soldOut(Boolean.TRUE.equals(product.getSoldOut()))
                                        .build();
                            })
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    /**
     * 사용자 cartItemId 기준 장바구니 내 상품 상세 응답 DTO (단건)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemsDetailResponseDto {
        private Long productId;
        private String productName;
        private String brand;
        private String category;
        private Integer price;
        private Integer quantity;
        private Boolean soldOut;
        private LocalDateTime createdAt;

        // 단건 변환
        public static CartItemsDetailResponseDto fromEntity(CartItemEntity item) {
            ProductEntity product = item.getProduct();
            CartEntity cart = item.getCart();

            return CartItemsDetailResponseDto.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .brand(product.getBrand())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .soldOut(Boolean.TRUE.equals(product.getSoldOut()))
                    .createdAt(cart != null ? cart.getCreatedAt() : null)
                    .build();
        }
    }

    /**
     * 장바구니 상품 추가 응답 DTO (POST)
     * - carId + createdAt + items 구조
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCartItemsResponseDto {
        private LocalDateTime createdAt;
        private List<CartItem> items;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CartItem {
            private Long cartItemId;
            private Long productId;
            private String productName;
            private Integer quantity;
            private Integer price;
            private Boolean soldOut;
        }

        public static AddCartItemsResponseDto fromEntity(CartEntity cart) {
            return AddCartItemsResponseDto.builder()
                    .createdAt(cart.getCreatedAt())
                    .items(cart.getItems().stream()
                            .map(item -> {
                                ProductEntity product = item.getProduct();
                                return CartItem.builder()
                                        .cartItemId(item.getId())
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .quantity(item.getQuantity())
                                        .price(product.getPrice())
                                        .soldOut(Boolean.TRUE.equals(product.getSoldOut()))
                                        .build();
                            })
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    /**
     * 장바구니 상품 추가 요청 DTO (POST)
     * - RequestBody로 전달받는 구조
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCartItemsRequestDto {
            @Schema(description = "상품 ID (추가할 상품의 고유 식별자)", example = "1001")
            @NotNull(message = "상품 ID는 필수입니다.")
            private Long productId;

            @Schema(description = "추가할 상품 수량 (1 이상)", example = "2")
            @NotNull(message = "수량은 필수입니다.")
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
            private Integer quantity;
    }



}
