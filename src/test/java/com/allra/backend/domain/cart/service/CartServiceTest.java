package com.allra.backend.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.repository.CartRepository;
import com.allra.backend.domain.product.entity.ProductEntity;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("사용자 장바구니 조회 (userId 기준)")
    void testGetUserCartsByUserId() {
        ProductEntity product = ProductEntity.builder()
                .id(100L)
                .name("테스트상품")
                .price(10000)
                .brand("BrandA")
                .category("전자제품")
                .soldOut(false)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        CartEntity cart = CartEntity.builder()
                .id(1L)
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(cartRepository.findUserCartsByUserId(anyLong()))
                .thenReturn(List.of(cart));

        List<CartDto.UserCartResponseDto> result = cartService.getUserCartsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("테스트상품");
        assertThat(result.get(0).getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("사용자 특정 장바구니 상세 조회 성공 (cartId 기준)")
    void testGetCartsDetailByCartId() {
        ProductEntity product = ProductEntity.builder()
                .id(200L)
                .name("마우스")
                .price(25000)
                .brand("Logi")
                .category("주변기기")
                .soldOut(false)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(10L)
                .product(product)
                .quantity(3)
                .build();

        CartEntity cart = CartEntity.builder()
                .id(2L)
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(cartRepository.findCartsByUserIdAndCartId(anyLong(), anyLong()))
                .thenReturn(List.of(cart));

        List<CartDto.CartsIdDetailResponseDto> result = cartService.getCartsDetailByCartId(1L, 2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getProductName()).isEqualTo("마우스");
        assertThat(result.get(0).getItems().get(0).getPrice()).isEqualTo(25000);
        assertThat(result.get(0).getItems().get(0).getQuantity()).isEqualTo(3);
    }


    @Test
    @DisplayName("사용자 장바구니 아이템 단건 조회 성공")
    void testGetCartItemDetail_Success() {
        ProductEntity product = ProductEntity.builder()
                .id(10L)
                .name("노트북")
                .price(1200000)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(5L)
                .product(product)
                .quantity(1)
                .build();

        Mockito.when(cartRepository.findCartItemByIds(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.of(item));

        var result = cartService.getCartItemDetail(1L, 2L, 5L);
        assertThat(result.getProductName()).isEqualTo("노트북");
        assertThat(result.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 장바구니 아이템 단건 조회 실패 (존재하지 않음)")
    void testGetCartItemDetail_NotFound() {
        Mockito.when(cartRepository.findCartItemByIds(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        var result = cartService.getCartItemDetail(1L, 2L, 5L);
        assertThat(result).isNull();
    }
}   
