package com.allra.backend.domain.cart.dto;

import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.product.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CartDtoTest {
 @Test
    @DisplayName("UserCartResponseDto.fromEntity() 변환 성공")
    void testUserCartResponseDtoFromEntity() {
        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .name("테스트 상품")
                .brand("BrandA")
                .category("전자제품")
                .price(10000)
                .soldOut(false)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(11L)
                .product(product)
                .quantity(2)
                .build();

        CartEntity cart = CartEntity.builder()
                .id(100L)
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();

        List<CartDto.UserCartResponseDto> result = CartDto.UserCartResponseDto.fromEntity(cart);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("테스트 상품");
        assertThat(result.get(0).getPrice()).isEqualTo(10000);
        assertThat(result.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("CartsIdDetailResponseDto.fromEntity() 변환 성공")
    void testCartsIdDetailResponseDtoFromEntity() {
        ProductEntity product = ProductEntity.builder()
                .id(2L)
                .name("마우스")
                .brand("Logi")
                .category("주변기기")
                .price(25000)
                .soldOut(true)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(22L)
                .product(product)
                .quantity(3)
                .build();

        CartEntity cart = CartEntity.builder()
                .id(200L)
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();

        CartDto.CartsIdDetailResponseDto result = CartDto.CartsIdDetailResponseDto.fromEntity(cart);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("마우스");
        assertThat(result.getItems().get(0).getSoldOut()).isTrue();
    }

    @Test
    @DisplayName("CartItemsDetailResponseDto.fromEntity() 변환 성공 및 null-safe 확인")
    void testCartItemsDetailResponseDtoFromEntity() {
        ProductEntity product = ProductEntity.builder()
                .id(3L)
                .name("노트북")
                .brand("LG")
                .category("전자제품")
                .price(1200000)
                .soldOut(false)
                .build();

        // cart 없이 생성 (null-safe test)
        CartItemEntity item = CartItemEntity.builder()
                .id(33L)
                .product(product)
                .quantity(1)
                .build();

        CartDto.CartItemsDetailResponseDto result = CartDto.CartItemsDetailResponseDto.fromEntity(item);

        assertThat(result.getProductName()).isEqualTo("노트북");
        assertThat(result.getCreatedAt()).isNull(); // cart가 null이므로 null이어야 함
    }
}
