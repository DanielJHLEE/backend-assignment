package com.allra.backend.global.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 페이지네이션 응답 Wrapper 클래스
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {

    private List<T> row; // 실제 데이터 목록
    private PageInfo pageInfo; // 페이지 정보

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean isLast;
    }

    // Page<T> → PageResponse<T> 변환 유틸리티
    public static <T> PageResponseDto<T> from(Page<T> page) {
        return PageResponseDto.<T>builder()
                .row(page.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page.getNumber() + 1)
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .isLast(page.isLast())
                        .build())
                .build();
    }
}
