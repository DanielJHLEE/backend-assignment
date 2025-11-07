package com.allra.backend.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.allra.backend.domain.product.dto.ProductDto;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductEntity product;

    @BeforeEach
    void setup() {
        product = ProductEntity.builder()
                .id(1L)
                .name("테스트상품")
                .brand("ALLRA")
                .category("전자제품")
                .price(10000)
                .stock(10)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("상품 목록 조회 - 정상 동작")
    void getAllProducts_shouldReturnPagedList() {
        // given
        var pageable = PageRequest.of(0, 10);
        Page<ProductEntity> mockPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.searchProducts(any(), any(), any(), any(), any()))
                .thenReturn(mockPage);

        // when
        Page<ProductDto.ProductResponseDto> result =
                productService.getAllProducts(1, 10, null, null, null, null);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스트상품");
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("ALLRA");
        assertThat(result.getContent().get(0).getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("상품 상세 조회 - 존재하는 상품일 경우")
    void getProductById_shouldReturnProduct() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        Optional<ProductDto.ProductResponseDto> result = productService.getProductById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("테스트상품");
        assertThat(result.get().getBrand()).isEqualTo("ALLRA");
    }

    @Test
    @DisplayName("상품 상세 조회 - 존재하지 않는 상품일 경우")
    void getProductById_shouldReturnEmpty() {
        // given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<ProductDto.ProductResponseDto> result = productService.getProductById(999L);

        // then
        assertThat(result).isEmpty();
    }
    
}
