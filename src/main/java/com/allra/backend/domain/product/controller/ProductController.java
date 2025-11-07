package com.allra.backend.domain.product.controller;

import com.allra.backend.domain.product.dto.ProductDto;
import com.allra.backend.domain.product.service.ProductService;
import com.allra.backend.global.dto.ApiResponseDto;
import com.allra.backend.global.dto.PageResponseDto;
import com.allra.backend.docs.swagger.SwaggerTags;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 📦 Product Controller
 * 상품 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.PRODUCT_NAME, description = SwaggerTags.PRODUCT_DESC)
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 목록 조회 (페이징 + 검색)
     */
    @GetMapping
    @Operation(
        summary = "상품 목록 조회 (페이징 + 검색)",
        description = SwaggerTags.PRODUCT_GET_ALL_DESC
    )
    public ApiResponseDto<PageResponseDto<ProductDto.ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        Page<ProductDto.ProductResponseDto> products =
                productService.getAllProducts(page, size, category, name, minPrice, maxPrice);

        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), PageResponseDto.from(products));
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "상품 상세 조회",
        description = SwaggerTags.PRODUCT_GET_DETAIL_DESC
    )
    public ResponseEntity<ApiResponseDto<ProductDto.ProductResponseDto>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), product)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }
}
