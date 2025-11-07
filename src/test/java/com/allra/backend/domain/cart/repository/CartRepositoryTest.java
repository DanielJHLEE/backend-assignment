package com.allra.backend.domain.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.user.entity.UserEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test") // application-test.yml 설정 사용
@Transactional
class CartRepositoryTest {
@Autowired
    private CartRepository cartRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("사용자(userId) 기준 장바구니 목록 조회 성공")
    void testFindUserCartsByUserId() {
        // 더미 유저 생성
        UserEntity user = UserEntity.builder()
                .name("테스트유저")
                .email("user1@test.com")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(user);

        // given
        ProductEntity product = ProductEntity.builder()
                .name("테스트 상품")
                .brand("BrandA")
                .category("전자제품")
                .price(10000)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(product);

        CartEntity cart = CartEntity.builder()
                .user(user) // 유저 연결
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(cart);

        CartItemEntity item = CartItemEntity.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();
        entityManager.persist(item);

        entityManager.flush();
        entityManager.clear();

        // when
        List<CartEntity> result = cartRepository.findUserCartsByUserId(user.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getProduct().getName()).isEqualTo("테스트 상품");
    }

    @Test
    @DisplayName("사용자(userId, cartId) 기준 장바구니 상세 조회 성공")
    void testFindCartsByUserIdAndCartId() {
        // 더미 유저 생성
        UserEntity user = UserEntity.builder()
                .name("테스트유저2")
                .email("user2@test.com")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(user);

        // given
        ProductEntity product = ProductEntity.builder()
                .name("마우스")
                .brand("Logi")
                .category("주변기기")
                .price(25000)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(product);

        CartEntity cart = CartEntity.builder()
                .user(user) // 유저 연결
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(cart);

        CartItemEntity item = CartItemEntity.builder()
                .cart(cart)
                .product(product)
                .quantity(3)
                .build();
        entityManager.persist(item);

        entityManager.flush();
        entityManager.clear();

        // when
        List<CartEntity> result = cartRepository.findCartsByUserIdAndCartId(user.getId(), cart.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getProduct().getName()).isEqualTo("마우스");
    }

    @Test
    @DisplayName("사용자(userId, cartId, cartItemId) 기준 장바구니 아이템 단건 조회 성공")
    void testFindCartItemByIds() {
        // 더미 유저 생성
        UserEntity user = UserEntity.builder()
                .name("테스트유저3")
                .email("user3@test.com")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(user);

        // given
        ProductEntity product = ProductEntity.builder()
                .name("키보드")
                .brand("Logitech")
                .category("주변기기")
                .price(80000)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(product);

        CartEntity cart = CartEntity.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(cart);

        CartItemEntity item = CartItemEntity.builder()
                .cart(cart)
                .product(product)
                .quantity(1)
                .build();
        entityManager.persist(item);

        entityManager.flush();
        entityManager.clear();

        // when
        var result = cartRepository.findCartItemByIds(user.getId(), cart.getId(), item.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getProduct().getName()).isEqualTo("키보드");
        assertThat(result.get().getQuantity()).isEqualTo(1);
    }
}
