package com.allra.backend.domain.cart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
                .filter(list -> !list.isEmpty())
                .map(list -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), list)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }

    /**
     * 장바구니 아이템 단건 상세 조회
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
     * 상품 장바구니 추가
     */
    @PostMapping
    @Operation(
        summary = "상품을 장바구니에 추가",
        description = SwaggerTags.CART_POST_ADD_ITEM_DESC
    )
    public ResponseEntity<ApiResponseDto<CartDto.AddCartItemsResponseDto>> addProductsToCart(
            @RequestBody CartDto.AddCartItemsRequestDto request) {

        CartDto.AddCartItemsResponseDto response = cartService.addProductsToCart(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("상품이 장바구니에 추가되었습니다.", response));
    }

    /**
     * 장바구니 상품 수량 수정
     */
    @PatchMapping("/{cartId}/items/{cartItemId}")
    @Operation(
        summary = "장바구니 상품 수량 수정",
        description = SwaggerTags.CART_PATCH_UPDATE_ITEM_DESC
    )
    public ResponseEntity<ApiResponseDto<CartDto.UpdateCartItemResponseDto>> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartDto.UpdateCartItemRequestDto request) {

        CartDto.UpdateCartItemResponseDto response = cartService.updateCartItemQuantity(userId, cartId, cartItemId, request);

        return ResponseEntity.ok(ApiResponseDto.success("상품 수량이 수정되었습니다.", response));
    }

    /**
     * 장바구니 개별 상품 삭제
     * 
     * 특정 장바구니(cartId) 내에서 상품(cartItemId)을 삭제합니다.
     */
    @DeleteMapping("/{cartId}/items/{cartItemId}")
    @Operation(
        summary = "장바구니 상품 삭제",
        description = SwaggerTags.CART_DELETE_ITEM_DESC
    )
    public ResponseEntity<ApiResponseDto<Void>> deleteCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @PathVariable Long cartItemId) {

        cartService.deleteCartItem(userId, cartId, cartItemId);
        return ResponseEntity.ok(ApiResponseDto.success("상품이 장바구니에서 삭제되었습니다.", null));
    }

    /**
     * 🗑 장바구니 전체 삭제
     * 
     * 사용자의 장바구니(cartId) 전체를 삭제합니다.
     */
    @DeleteMapping("/{cartId}")
    @Operation(
        summary = "장바구니 전체 삭제",
        description = SwaggerTags.CART_DELETE_CART_DESC
    )
    public ResponseEntity<ApiResponseDto<Void>> deleteEntireCart(
            @PathVariable Long userId,
            @PathVariable Long cartId) {

        cartService.deleteEntireCart(userId, cartId);
        return ResponseEntity.ok(ApiResponseDto.success("장바구니가 모두 비워졌습니다.", null));
    }


}
