package com.allra.backend.domain.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allra.backend.domain.product.dto.ProductDto;
import com.allra.backend.domain.product.service.ProductService;

import com.allra.backend.global.dto.ApiResponseDto;
import com.allra.backend.global.dto.PageResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * Product Controller
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 목록 조회 (페이징 + 검색)
     */
    @GetMapping
    public ApiResponseDto<PageResponseDto<ProductDto.ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,      // 페이지 번호
            @RequestParam(defaultValue = "10") int size,     // 페이지 크기
            @RequestParam(required = false) String category, // 카테고리 필터
            @RequestParam(required = false) String name,     // 상품명 검색
            @RequestParam(required = false) Integer minPrice,// 가격 하한
            @RequestParam(required = false) Integer maxPrice // 가격 상한
    ) {
        Page<ProductDto.ProductResponseDto> products = 
            productService.getAllProducts(page, size, category, name, minPrice, maxPrice);

        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), PageResponseDto.from(products));
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ProductDto.ProductResponseDto>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(user -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), user)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND.getReasonPhrase())));
    }
    
}
