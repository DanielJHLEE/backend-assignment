package com.allra.backend.domain.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.repository.CartRepository;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.repository.ProductRepository;
import com.allra.backend.domain.user.entity.UserEntity;
import com.allra.backend.domain.user.repository.UserRepository;
import com.allra.backend.global.exception.BusinessException;
import com.allra.backend.global.validator.AuthValidator;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Cart Service
 * 장바구니 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final EntityManager entityManager;

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

    /**
     * 상품을 장바구니에 추가
     */
    @Transactional
    public CartDto.AddCartItemsResponseDto addProductsToCart(CartDto.AddCartItemsRequestDto request) {
        // 유저, 상품 조회
        UserEntity user = userRepository.getByIdOrThrow(request.getUserId());
        ProductEntity product = productRepository.getByIdOrThrow(request.getProductId());

        // 장바구니 조회 또는 생성
        CartEntity cart = getOrCreateCart(user);

        // 장바구니 아이템 추가 or 수량 1 증가
        addOrUpdateCartItem(cart, product);

        // DB 반영
        entityManager.flush();
        entityManager.refresh(cart);

        // 응답 변환
        return CartDto.AddCartItemsResponseDto.fromEntity(cart);
    }

    /**
     * 장바구니가 없으면 생성하고, 있으면 기존 장바구니 반환
     */
    private CartEntity getOrCreateCart(UserEntity user) {
        return cartRepository.findUserCartsByUserId(user.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    CartEntity newCart = CartEntity.builder()
                            .user(user)
                            .build();
                    entityManager.persist(newCart);
                    return newCart;
                });
    }

    /**
     * 장바구니에 상품 추가 또는 수량 증가
     * 
     * - 이미 동일한 상품이 있으면 quantity + 1
     * - 없으면 새로 추가 (quantity = 1)
     */
    private void addOrUpdateCartItem(CartEntity cart, ProductEntity product) {
        cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        // 이미 존재 → 수량 +1
                        item -> item.setQuantity(item.getQuantity() + 1),
                        // 존재하지 않음 → 새로 추가 (기본 수량 1)
                        () -> {
                            CartItemEntity newItem = CartItemEntity.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(1)
                                    .build();
                            cart.getItems().add(newItem);
                            entityManager.persist(newItem);
                        }
                );
    }

    /**
     * 장바구니 상품 수량 수정
     *
     * 기존 장바구니 상품의 수량을 요청된 값으로 갱신합니다.
     * - 존재하지 않는 cartItemId일 경우 BusinessException 발생
     * - 수량은 DTO(@Min(1)) 레벨에서 이미 검증됨
     */
    @Transactional
    public CartDto.UpdateCartItemResponseDto updateCartItemQuantity(
            Long userId,
            Long cartId,
            Long cartItemId,
            CartDto.UpdateCartItemRequestDto request
    ) {
        CartItemEntity cartItem = cartRepository.findCartItemByIds(userId, cartId, cartItemId)
                .orElseThrow(() -> new BusinessException("해당 장바구니 아이템을 찾을 수 없습니다."));
        
        // 수량 업데이트
        cartItem.setQuantity(request.getQuantity());

        entityManager.flush();
        entityManager.refresh(cartItem);

        return CartDto.UpdateCartItemResponseDto.fromEntity(cartItem);
    }

    /**
     * 🗑 개별 상품 삭제
     */
    @Transactional
    public void deleteCartItem(Long userId, Long cartId, Long cartItemId) {
        // 존재하지 않으면 BusinessException 발생
        CartItemEntity cartItem = cartRepository.findCartItemByIds(userId, cartId, cartItemId)
                .orElseThrow(() -> new BusinessException("삭제할 장바구니 상품을 찾을 수 없습니다."));

        AuthValidator.validateOwnership(cartItem.getCart().getUser().getId(), userId, "장바구니 상품");

        entityManager.remove(cartItem);
    }

    /**
     * 장바구니 전체 삭제
     */
    @Transactional
    public void deleteEntireCart(Long userId, Long cartId) {
        // cart 조회 (없으면 예외)
        CartEntity cart = cartRepository.findCartsByUserIdAndCartId(userId, cartId).stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("삭제할 장바구니를 찾을 수 없습니다."));

        AuthValidator.validateOwnership(cart.getUser().getId(), userId, "장바구니");

        entityManager.remove(cart);
    }


}
