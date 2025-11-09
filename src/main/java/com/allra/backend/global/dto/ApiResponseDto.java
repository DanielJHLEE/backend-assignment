package com.allra.backend.global.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공통 API 응답 Wrapper 클래스
 * 모든 API 응답을 {status, message, data} 형태로 통일
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    private int status;    // HTTP 상태 코드 (200, 404 등)
    private String message; // 설명 메시지
    private T data;         // 실제 응답 데이터

    // 성공 응답 (data만 전달)
    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
                .status(HttpStatus.OK.value())
                .message(null)
                .data(data)
                .build();
    }

    // 성공 응답 (message + data)
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return ApiResponseDto.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    // 실패 (404 Not Found)
    public static <T> ApiResponseDto<T> notFound(String message) {
        return ApiResponseDto.<T>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(message)
                .build();
    }

    // 실패 (400 Bad Request 등)
    public static <T> ApiResponseDto<T> fail(HttpStatus status, String message) {
        return ApiResponseDto.<T>builder()
                .status(status.value())
                .message(message)
                .build();
    }

    // 실패 응답 (message만 받는 간단 버전)
    public static <T> ApiResponseDto<T> fail(String message) {
        return ApiResponseDto.<T>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }

    
    // 결제 예외용 (PaymentErrorCode 기반)
    public static <T> ApiResponseDto<T> error(String errorCode, String message) {
        return ApiResponseDto.<T>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(String.format("[%s] %s", errorCode, message))
                .build();
    }
    
}
