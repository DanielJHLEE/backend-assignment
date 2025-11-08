package com.allra.backend.domain.cart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allra.backend.docs.swagger.SwaggerTags;
import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.service.CartService;
import com.allra.backend.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Cart Controller
 * 장바구니 관련 API를 처리하는 컨트롤러 클래스
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
     * 장바구니 상세 조회
     */
    @GetMapping("/{cartId}")
    @Operation(
        summary = "장바구니 상세 조회",
        description = SwaggerTags.CART_GET_DETAIL_DESC
    )
    public ResponseEntity<ApiResponseDto<List<CartDto.CartsIdDetailResponseDto>>> getCartsDetail(
            @PathVariable Long userId,
            @PathVariable Long cartId) {

        List<CartDto.CartsIdDetailResponseDto> cartDetails = cartService.getCartsDetailByCartId(userId, cartId);

        return Optional.ofNullable(cartDetails)
                .filter(list -> !list.isEmpty()) // 비어있지 않으면 OK
                .map(list -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), list)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }

    /**
     * 📦 장바구니 아이템 단건 상세 조회
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

    /**
     * ➕ 장바구니 상품 추가
     * <p>
     * 사용자의 장바구니에 상품을 추가하거나,
     * 이미 존재하는 상품의 경우 수량을 증가시킵니다.<br><br>
     *
     * ✅ <b>요청 예시 (JSON)</b><br>
     * <pre>{
     *   "productId": 1001,
     *   "quantity": 2
     * }</pre>
     *
     * ✅ <b>응답 형식</b><br>
     * <code>ApiResponseDto&lt;AddCartItemsResponseDto&gt;</code><br><br>
     *
     * 성공 시 <code>201 Created</code> 응답을 반환합니다.
     */
     @PostMapping
     @Operation(summary = "상품을 장바구니에 추가", description = SwaggerTags.CART_POST_ADD_ITEM_DESC)
     public ResponseEntity<ApiResponseDto<CartDto.AddCartItemsResponseDto>> addProductsToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartDto.AddCartItemsRequestDto request) {

        CartDto.AddCartItemsResponseDto response = cartService.addProductsToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("상품이 장바구니에 추가되었습니다.", response));
    }

    
}
