package com.allra.backend.global.exception;

import com.allra.backend.domain.payment.exception.PaymentException;
import com.allra.backend.global.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 🌐 GlobalExceptionHandler
 *
 * - 애플리케이션 전역에서 발생하는 예외를 처리하고
 *   일관된 JSON 응답(ApiResponseDto)으로 반환한다.
 * - 비즈니스/결제/유효성/서버 예외를 구분하여 처리.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1️⃣ 유효성 검증 실패 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("[Validation Error] {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(HttpStatus.BAD_REQUEST, message));
    }

    /**
     * 2️⃣ NotFoundException (리소스 존재하지 않음)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleNotFound(NotFoundException e) {
        log.warn("[Not Found] {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    /**
     * 3️⃣ BusinessException (비즈니스 로직 위반)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<?>> handleBusiness(BusinessException e) {
        log.warn("[Business Exception] {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.fail(HttpStatus.CONFLICT, e.getMessage()));
    }

    /**
     * 4️⃣ PaymentException (결제 관련 예외)
     * - PaymentService 내에서 발생한 결제 로직 오류
     * - ErrorCode + 메시지 기반으로 응답
     */
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponseDto<?>> handlePaymentException(PaymentException e) {
        log.error("[Payment Exception] Code={}, Message={}", e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(e.getErrorCode().getCode(), e.getMessage()));
    }

    /**
     * 5️⃣ 그 외 모든 예외 (서버 내부 오류)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleGeneral(Exception e) {
        log.error("[Unhandled Exception] {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
    }
}
