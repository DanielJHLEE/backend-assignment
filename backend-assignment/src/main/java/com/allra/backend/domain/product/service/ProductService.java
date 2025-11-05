package com.allra.backend.domain.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allra.backend.domain.product.dto.ProductDto;
import com.allra.backend.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 전체 상품 조회
    public List<ProductDto.ProductResponseDto> getAllProducts() {
       return productRepository.findAll().stream()
               .map(ProductDto.ProductResponseDto::fromEntity) // 변환 메서드 사용
               .toList();
    }

    // ID로 상품 조회
    public Optional<ProductDto.ProductResponseDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDto.ProductResponseDto::fromEntity);
    }
    
}
