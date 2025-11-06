package com.allra.backend.domain.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.allra.backend.domain.product.dto.ProductDto;
import com.allra.backend.domain.product.service.ProductService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    /**
     * 상품 목록 조회 테스트 (Mock 기반)
     */
    @Test
    void getAllProducts_shouldReturnPagedList() throws Exception {
        // given
        ProductDto.ProductResponseDto product = ProductDto.ProductResponseDto.builder()
                .id(1L)
                .name("테스트상품")
                .brand("ALLRA")
                .category("전자제품")
                .price(10000)
                .stock(5)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();

        var mockPage = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);

        // mock 설정 (파라미터 전부 any로 유연하게)
        when(productService.getAllProducts(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/products")
                        .param("page", "1")   // 내부에서 0으로 조정되므로 1로 보내야 맞음
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.row[0].id").value(1))
                .andExpect(jsonPath("$.data.row[0].name").value("테스트상품"))
                .andExpect(jsonPath("$.data.row[0].brand").value("ALLRA"))
                .andExpect(jsonPath("$.data.row[0].category").value("전자제품"))
                .andExpect(jsonPath("$.data.row[0].price").value(10000))
                .andExpect(jsonPath("$.data.row[0].stock").value(5))
                .andExpect(jsonPath("$.data.row[0].soldOut").value(false));
    }

    // 상품 상세 조회 - 200 OK 테스트 (Mock 기반)
    @Test
    void getProductById_shouldReturn200_whenFound() throws Exception {
        ProductDto.ProductResponseDto dummy = ProductDto.ProductResponseDto.builder()
                .id(1L)
                .name("테스트상품")
                .brand("ALLRA")
                .category("전자제품")
                .price(10000)
                .stock(50)
                .soldOut(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(productService.getProductById(1L)).thenReturn(Optional.of(dummy));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("테스트상품"))
                .andExpect(jsonPath("$.data.brand").value("ALLRA"));
    }

    // 상품 상세 조회 - 404 Not Found 테스트 (Mock 기반)
    @Test
    void getProductById_shouldReturn404_whenNotFound() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

}
