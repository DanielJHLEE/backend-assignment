package com.allra.backend.domain.cart.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.service.CartService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("사용자 장바구니 목록 조회 성공")
    void testGetUserCarts() throws Exception {
        // given
        CartDto.UserCartResponseDto mockCart = CartDto.UserCartResponseDto.builder()
                .cartId(1L)
                .cartItemId(1L)
                .productId(100L)
                .productName("테스트 상품")
                .brand("BrandA")
                .category("전자제품")
                .price(50000)
                .soldOut(false)
                .quantity(2)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(cartService.getUserCartsByUserId(anyLong()))
                .thenReturn(List.of(mockCart));

        // when & then
        mockMvc.perform(get("/api/users/1/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data[0].price").value(50000));
    }

    @Test
    @DisplayName("특정 장바구니 상세 조회 성공")
    void testGetCartsDetail() throws Exception {
        CartDto.CartsIdDetailResponseDto.CartItemDetail item =
                CartDto.CartsIdDetailResponseDto.CartItemDetail.builder()
                        .cartItemId(1L)
                        .productId(100L)
                        .productName("테스트 상품")
                        .quantity(3)
                        .price(10000)
                        .soldOut(false)
                        .build();

        CartDto.CartsIdDetailResponseDto cartDetail =
                CartDto.CartsIdDetailResponseDto.builder()
                        .createdAt(LocalDateTime.now())
                        .items(List.of(item))
                        .build();

        Mockito.when(cartService.getCartsDetailByCartId(anyLong(), anyLong()))
                .thenReturn(List.of(cartDetail));

        mockMvc.perform(get("/api/users/1/carts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].items[0].productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data[0].items[0].quantity").value(3));
    }

    @Test
    @DisplayName("장바구니 내 상품 단건 조회 성공")
    void testGetCartItemDetail() throws Exception {
        CartDto.CartItemsDetailResponseDto itemDetail =
                CartDto.CartItemsDetailResponseDto.builder()
                        .productId(100L)
                        .productName("노트북")
                        .brand("LG")
                        .category("전자제품")
                        .price(1200000)
                        .quantity(1)
                        .soldOut(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        Mockito.when(cartService.getCartItemDetail(anyLong(), anyLong(), anyLong()))
                .thenReturn(itemDetail);

        mockMvc.perform(get("/api/users/1/carts/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("노트북"))
                .andExpect(jsonPath("$.data.price").value(1200000));
    }


}
