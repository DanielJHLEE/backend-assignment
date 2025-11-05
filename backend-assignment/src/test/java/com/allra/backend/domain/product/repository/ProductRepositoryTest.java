package com.allra.backend.domain.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.allra.backend.domain.product.entity.ProductEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest // productRepository 빈 주입 위해
@ActiveProfiles("test") // application-test.yml 사용
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager; // JPA EntityManager -- 테스트용

    @Test
    @DisplayName("상품명 검색이 대소문자 구분 없이 정상 동작해야 한다")
    void searchProducts_withNameIgnoreCase() {
        // given
        entityManager.persist(ProductEntity.builder()
                .name("Galaxy S24")
                .brand("SAMSUNG")
                .category("전자제품")
                .price(1200000)
                .stock(10)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.flush();

        // when
        Page<ProductEntity> result = productRepository.searchProducts(
                null, "galaxy", null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("SAMSUNG");
    }

    @Test
    @DisplayName("모든 조건이 없을 때 전체 상품이 조회되어야 한다")
    void searchProducts_withoutConditions_shouldReturnAll() {
        // given
        entityManager.persist(ProductEntity.builder()
                .name("노트북")
                .brand("LG")
                .category("전자제품")
                .price(1000000)
                .stock(5)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.persist(ProductEntity.builder()
                .name("청소기")
                .brand("DYSON")
                .category("생활가전")
                .price(500000)
                .stock(3)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.flush();

        // when
        Page<ProductEntity> result = productRepository.searchProducts(
                null, null, null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("카테고리와 가격 범위 조건으로 필터링이 정상 동작해야 한다")
    void searchProducts_withCategoryAndPriceRange() {
        // given
        entityManager.persist(ProductEntity.builder()
                .name("아이폰 15")
                .brand("APPLE")
                .category("전자제품")
                .price(1500000)
                .stock(10)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.persist(ProductEntity.builder()
                .name("냉장고")
                .brand("LG")
                .category("가전")
                .price(800000)
                .stock(5)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.flush();

        // when
        Page<ProductEntity> result = productRepository.searchProducts(
                "전자제품", null, 1000000, 1600000, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("아이폰 15");
    }

    @Test
    @DisplayName("카테고리, 가격, 상품명 조건이 모두 일치할 때만 결과가 반환되어야 한다")
    void searchProducts_withCategoryPriceAndName() {
        // given
        entityManager.persist(ProductEntity.builder()
                .name("아이폰15프로")
                .brand("APPLE")
                .category("전자제품")
                .price(1500000)
                .stock(10)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.persist(ProductEntity.builder()
                .name("아이폰14")
                .brand("APPLE")
                .category("전자제품")
                .price(1200000)
                .stock(5)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.persist(ProductEntity.builder()
                .name("냉장고")
                .brand("LG")
                .category("가전")
                .price(800000)
                .stock(5)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build());

        entityManager.flush();

        // when
        Page<ProductEntity> result = productRepository.searchProducts(
                "전자제품", "아이폰15", 1000000, 1600000, PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("아이폰15프로");
    }

    @Test
    @DisplayName("상품 상세 조회 - 존재하는 ID일 때 Optional이 채워져 있어야 한다")
    void findById_shouldReturnPresentOptional() {
        ProductEntity entity = ProductEntity.builder()
                .name("갤럭시북")
                .brand("SAMSUNG")
                .category("전자제품")
                .price(1800000)
                .stock(5)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear(); // DB에서 다시 조회되도록

        Optional<ProductEntity> result = productRepository.findById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("갤럭시북");
    }

    @Test
    @DisplayName("상품 상세 조회 - 존재하지 않는 ID일 때 Optional.empty()를 반환해야 한다")
    void findById_shouldReturnEmptyOptionalWhenNotFound() {
        Optional<ProductEntity> result = productRepository.findById(999L);
        assertThat(result).isEmpty();
    }

}
