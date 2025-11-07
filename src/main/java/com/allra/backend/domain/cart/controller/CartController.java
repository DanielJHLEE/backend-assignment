package com.allra.backend.domain.cart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.service.CartService;
import com.allra.backend.global.dto.ApiResponseDto;
import com.allra.backend.docs.swagger.SwaggerTags;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 🛒 Cart Controller
 * 사용자별 장바구니 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/users/{userId}/carts")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.CART_NAME, description = SwaggerTags.CART_DESC)
public class CartController {

    private final CartService cartService;

    /**
     * 사용자 장바구니 목록 조회
     */
    @GetMapping
    @Operation(
        summary = "사용자 장바구니 목록 조회",
        description = SwaggerTags.CART_GET_ALL_DESC
    )
    public ApiResponseDto<List<CartDto.UserCartResponseDto>> getUserCarts(@PathVariable Long userId) {
        List<CartDto.UserCartResponseDto> cartItems = cartService.getUserCartsByUserId(userId);
        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), cartItems);
    }

    /**
     * 특정 장바구니(cartId) 상세 조회
     */
    @GetMapping("/{cartId}")
    @Operation(
        summary = "특정 장바구니 상세 조회",
        description = SwaggerTags.CART_GET_DETAIL_DESC
    )
    public ResponseEntity<ApiResponseDto<List<CartDto.CartsIdDetailResponseDto>>> getCartsDetail(
            @PathVariable Long userId,
            @PathVariable Long cartId) {

        List<CartDto.CartsIdDetailResponseDto> cartDetails = cartService.getCartsDetailByCartId(userId, cartId);

        return Optional.ofNullable(cartDetails)
                .filter(list -> !list.isEmpty())
                .map(list -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), list)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }

    /**
     * 장바구니 내 개별 상품(cartItemId) 상세 조회
     */
    @GetMapping("/{cartId}/items/{cartItemId}")
    @Operation(
        summary = "장바구니 아이템 상세 조회",
        description = SwaggerTags.CART_GET_ITEM_DETAIL_DESC
    )
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
