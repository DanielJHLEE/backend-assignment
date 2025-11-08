package com.allra.backend.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.entity.QProductEntity;
import com.allra.backend.global.exception.NotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * Product JPA Repository
 */
@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    /**
     * 상품 검색 (필터링 + 페이징)
     * @param category
     * @param name
     * @param minPrice
     * @param maxPrice
     * @param pageable
     * @return
     */
    public Page<ProductEntity> searchProducts(
            String category, String name, Integer minPrice, Integer maxPrice, Pageable pageable) {

        QProductEntity product = QProductEntity.productEntity;
        BooleanBuilder condition = new BooleanBuilder();

        if (category != null && !category.isBlank()) {
            // 대소문자 구분 및 공백 제거 후 검색
            condition.and(product.category.containsIgnoreCase(category.trim().replaceAll("\\s+", "")));
        }
        if (name != null && !name.isBlank()) {
            // 대소문자 구분 및 공백 제거 후 검색
            condition.and(product.name.containsIgnoreCase(name.trim().replaceAll("\\s+", "")));
        }
        if (minPrice != null) condition.and(product.price.goe(minPrice));
        if (maxPrice != null) condition.and(product.price.loe(maxPrice));

        List<ProductEntity> results = queryFactory
                .selectFrom(product)
                .where(condition)
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    /**
     * 상품 상세 조회
     * @param id
     * @return
     */
    public Optional<ProductEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(ProductEntity.class, id));
    }

    /**
     * 존재하지 않으면 예외 발생 (NotFoundException)
     */
    public ProductEntity getByIdOrThrow(Long productId) {
        ProductEntity product = entityManager.find(ProductEntity.class, productId);
        if (product == null)
            throw new NotFoundException("존재하지 않는 상품입니다.");
        return product;
    }

}
