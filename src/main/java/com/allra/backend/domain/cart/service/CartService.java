package com.allra.backend.domain.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.repository.CartRepository;

import lombok.RequiredArgsConstructor;

/**
 * Cart Service
 * 장바구니 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    /**
     * 사용자 장바구니 조회 (userId 기준)
     */
    public List<CartDto.UserCartResponseDto> getUserCartsByUserId(Long userId) {
    List<CartEntity> carts = cartRepository.findUserCartsByUserId(userId);
    return carts.stream()
                .flatMap(cart -> CartDto.UserCartResponseDto.fromEntity(cart).stream())
                .toList();
    }

    /**
     * 사용자 장바구니 ID로 장바구니 상세 조회 (cartId 기준) - 리스트
     */
    public List<CartDto.CartsIdDetailResponseDto> getCartsDetailByCartId(Long userId, Long cartId) {
        List<CartEntity> carts = cartRepository.findCartsByUserIdAndCartId(userId, cartId);
        return carts.stream()
            .map(CartDto.CartsIdDetailResponseDto::fromEntity)
            .toList();
    }

    /**
     * 사용자 장바구니 ID와 장바구니 아이템 ID로 개별 상품 상세 조회 - 단건
     */
    public CartDto.CartItemsDetailResponseDto getCartItemDetail(Long userId, Long cartId, Long cartItemId) {
        Optional<CartItemEntity> cartItemOpt = cartRepository.findCartItemByIds(userId, cartId, cartItemId);

        return cartItemOpt
                .map(CartDto.CartItemsDetailResponseDto::fromEntity)
                .orElse(null);
    }

    

}
