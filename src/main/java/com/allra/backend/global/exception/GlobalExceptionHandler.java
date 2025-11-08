package com.allra.backend.global.exception;

import com.allra.backend.global.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler
 * 애플리케이션 전역에서 발생하는 예외를 처리하여
 * 일관된 JSON 응답 형태(ApiResponseDto)로 반환한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 유효성 검증 실패 (ex: @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(HttpStatus.BAD_REQUEST, message));
    }

    /**
     * NotFoundException (리소스 존재하지 않음)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleNotFound(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    /**
     * BusinessException (비즈니스 로직 위반)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<?>> handleBusiness(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.fail(HttpStatus.CONFLICT, e.getMessage()));
    }

    /**
     * 그 외 모든 예외 (서버 내부 오류)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleGeneral(Exception e) {
        e.printStackTrace(); // 개발 중에만 로그 확인용
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
    }
}
