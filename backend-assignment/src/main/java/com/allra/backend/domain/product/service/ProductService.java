package com.allra.backend.domain.product.service;

import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.allra.backend.domain.product.dto.ProductDto;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 목록 조회 (페이징 + 필터)
     */
    public Page<ProductDto.ProductResponseDto> getAllProducts(
            int page, int size, String category, String name, Integer minPrice, Integer maxPrice) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductEntity> products = productRepository.findAll(pageable); // 기본 페이징 조회

        // TODO: 추후 QueryDSL로 카테고리, 이름, 가격 조건 필터링 확장 가능
        return products.map(ProductDto.ProductResponseDto::fromEntity);
    }

    /**
     * 상품 상세 조회
     */
    public Optional<ProductDto.ProductResponseDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDto.ProductResponseDto::fromEntity);
    }
    
}
