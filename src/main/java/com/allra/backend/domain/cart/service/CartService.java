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
    public CartDto.AddCartItemsResponseDto addProductsToCart(Long userId, CartDto.AddCartItemsRequestDto request) {
        UserEntity user = userRepository.getByIdOrThrow(userId);
        ProductEntity product = productRepository.getByIdOrThrow(request.getProductId());
        
        product.validateStock(request.getQuantity());

        CartEntity cart = getOrCreateCart(user);
        addOrUpdateCartItem(cart, product, request.getQuantity());

        entityManager.flush();
        entityManager.refresh(cart);

        return CartDto.AddCartItemsResponseDto.fromEntity(cart);
    }

    /**
     * 장바구니 없으면 생성 / 있으면 반환
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
     */
    private void addOrUpdateCartItem(CartEntity cart, ProductEntity product, int quantity) {
        cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> { // 이미 존재 → 수량 증가
                            int newQty = item.getQuantity() + quantity;
                            product.validateStock(newQty);
                            item.setQuantity(newQty);
                        },
                        () -> { // 없으면 새로 추가
                            CartItemEntity newItem = CartItemEntity.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(quantity)
                                    .build();
                            cart.getItems().add(newItem);
                            entityManager.persist(newItem);
                        }
                );
    }


    

}
