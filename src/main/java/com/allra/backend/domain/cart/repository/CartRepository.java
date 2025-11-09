package com.allra.backend.domain.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.entity.QCartEntity;
import com.allra.backend.domain.cart.entity.QCartItemEntity;
import com.allra.backend.domain.product.entity.QProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * Cart JPA Repository
 * - QueryDSL을 이용해 Cart / CartItem / Product를 조인
 * - 유저별 / 장바구니별 / 아이템별 조회 기능 제공
 */
@Repository
@RequiredArgsConstructor
public class CartRepository {

    private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

    /**
     * 사용자 장바구니 조회
     * - cart, cart_item, product 조인
     */
    public List<CartEntity> findUserCartsByUserId(Long userId) {
        QCartEntity cart = QCartEntity.cartEntity;
        QCartItemEntity item = QCartItemEntity.cartItemEntity;
        QProductEntity product = QProductEntity.productEntity;

        // 장바구니와 상품을 조인해서 한 번에 가져오기
        return queryFactory
                .selectFrom(cart)
                .distinct() // JPA레벨에서 엔티티 병합
                .leftJoin(cart.items, item).fetchJoin()
                .leftJoin(item.product, product).fetchJoin()
                .where(cart.user.id.eq(userId))
                .fetch();
    }

    /**
     * 사용자 장바구니 ID 기준 상세 조회
     * - 하나의 cartId에 여러 cartItem이 포함될 수 있으므로 List<CartEntity> 반환
     * - userId & cartId 일치하는 장바구니의 모든 아이템 조회
     */
    public List<CartEntity> findCartsByUserIdAndCartId(Long userId, Long cartId) {
        QCartEntity cart = QCartEntity.cartEntity;
        QCartItemEntity item = QCartItemEntity.cartItemEntity;
        QProductEntity product = QProductEntity.productEntity;

        return queryFactory.selectFrom(cart)
                .distinct() // JPA레벨에서 엔티티 병합
                .leftJoin(cart.items, item).fetchJoin()
                .leftJoin(item.product, product).fetchJoin()
                .where(
                        cart.user.id.eq(userId)
                        .and(cart.id.eq(cartId))
                )
                .fetch();
    }

    /**
     * 사용자 장바구니 내 개별 상품 단건 조회
     * - userId, cartId, cartItemId 모두 일치하는 데이터 조회
     */
    public Optional<CartItemEntity> findCartItemByIds(Long userId, Long cartId, Long cartItemId) {
        QCartItemEntity item = QCartItemEntity.cartItemEntity;
        QCartEntity cart = QCartEntity.cartEntity;
        QProductEntity product = QProductEntity.productEntity;

        return Optional.ofNullable(
                queryFactory.selectFrom(item)
                        .leftJoin(item.cart, cart).fetchJoin()
                        .leftJoin(item.product, product).fetchJoin()
                        .where(
                                cart.user.id.eq(userId)
                                .and(cart.id.eq(cartId))
                                .and(item.id.eq(cartItemId))
                        )
                        .fetchOne()
        );
    }

    /** 사용자 전체 장바구니 비우기 (결제 완료 시 호출) */
    public void deleteAllByUserId(Long userId) {
        QCartEntity qCart = QCartEntity.cartEntity;

        long deletedCount = queryFactory.delete(qCart)
                .where(qCart.user.id.eq(userId))
                .execute();

        if (deletedCount == 0) {
            throw new IllegalStateException("삭제할 장바구니가 없습니다. userId=" + userId);
        }
    }

	/** 장바구니 삭제 (유저 검증 포함) */
    public void deleteByUser(Long userId, CartEntity cart) {
        QCartEntity qCart = QCartEntity.cartEntity;

        long deleted = queryFactory.delete(qCart)
                .where(
                        qCart.id.eq(cart.getId())
                        .and(qCart.user.id.eq(userId))
                )
                .execute();

        if (deleted == 0) {
            throw new IllegalStateException("해당 사용자의 장바구니가 아닙니다.");
        }
    }

    /** 단순 삭제 (검증 불필요 시) */
    public void delete(CartEntity cart) {
        entityManager.remove(entityManager.contains(cart) ? cart : entityManager.merge(cart));
    }


}
