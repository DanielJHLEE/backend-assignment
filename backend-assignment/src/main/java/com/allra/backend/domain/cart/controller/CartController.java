package com.allra.backend.domain.cart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.service.CartService;
import com.allra.backend.global.dto.ApiResponseDto;

import lombok.RequiredArgsConstructor;



/**
 * Cart Controller
 * 장바구니 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/users/{userId}/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 사용자 장바구니 조회
     * - 특정 userId를 기준으로 장바구니 상품 목록 조회
     */
    @GetMapping
    public ApiResponseDto<List<CartDto.UserCartResponseDto>> getUserCarts(@PathVariable Long userId) {
        List<CartDto.UserCartResponseDto> cartItems = cartService.getUserCartsByUserId(userId);
        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), cartItems);
    }

    /**
     * 사용자 특정 장바구니(cartId) 상세 조회
     * - 해당 장바구니에 담긴 모든 상품 목록 조회
     */
    @GetMapping("/{cartId}")
    public ApiResponseDto<List<CartDto.CartsIdDetailResponseDto>> getCartsDetail(
            @PathVariable Long userId,
            @PathVariable Long cartId) {
        List<CartDto.CartsIdDetailResponseDto> cartDetails = cartService.getCartsDetailByCartId(userId, cartId);
        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), cartDetails);
    }

    /**
     * 사용자 장바구니 내 개별 상품(cartItemId) 상세 조회
     * - 장바구니 안의 특정 상품만 조회
     */
    @GetMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseDto<CartDto.CartItemsDetailResponseDto>> getCartItemDetail(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @PathVariable Long cartItemId) {

        return Optional.ofNullable(cartService.getCartItemDetail(userId, cartId, cartItemId))
                .map(cartItemDetail -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), cartItemDetail)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }


}
