package com.allra.backend.domain.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allra.backend.docs.swagger.SwaggerTags;
import com.allra.backend.domain.order.service.OrderService;
import com.allra.backend.domain.payment.dto.PaymentResultDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * OrderController
 *
 * 주문 및 결제 관련 엔드포인트 관리
 * ---------------------------------------------------------
 * [Flow]
 * 1️. 주문 생성 (장바구니 기반)
 * 2️. 결제 요청 (주문 ID 기반)
 * 3️. 결제 결과 조회 (Mock API 상태 확인)
 * 4️. 주문 취소 (Mock API 취소 및 로그 저장)
 * ---------------------------------------------------------
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.ORDER_NAME, description = SwaggerTags.ORDER_DESC)
public class OrderController {

    private final OrderService orderService;

    /**
     * 1️. 주문 생성
     *
     * - 사용자의 장바구니를 기반으로 주문 생성
     * - 장바구니 내 모든 상품 금액을 합산하여 주문 총액 계산
     * - Mock API에 주문 생성 요청
     * - 결제는 아직 진행되지 않음 (결제는 별도 요청)
     */
    @PostMapping("/{userId}")
    @Operation(
        summary = "주문 생성",
        description = SwaggerTags.ORDER_CREATE_DESC
    )
    public ResponseEntity<PaymentResultDto.OrderCreateResponse> createOrder(
            @PathVariable Long userId) {

        PaymentResultDto.OrderCreateResponse response = orderService.createOrder(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 2️. 결제 요청
     *
     * - 생성된 주문 ID를 기반으로 결제 시도
     * - Mock 결제 API 호출 → PENDING / SUCCESS / FAILED 상태 반환
     */
    @PostMapping("/{orderId}/payment")
    @Operation(
        summary = "결제 요청",
        description = SwaggerTags.ORDER_PAYMENT_REQUEST_DESC
    )
    public ResponseEntity<PaymentResultDto.OrderResultResponse> processPayment(
            @PathVariable String orderId) {

        PaymentResultDto.OrderResultResponse response = orderService.processPayment(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 3️. 결제 결과 조회
     *
     * - 결제 상태를 Mock API에서 조회 (PENDING → SUCCESS / FAILED)
     * - 실제 결제 완료 여부를 확인할 때 사용
     */
    @GetMapping("/{orderId}/payment/result")
    @Operation(
        summary = "결제 결과 조회",
        description = SwaggerTags.ORDER_PAYMENT_RESULT_DESC
    )
    public ResponseEntity<PaymentResultDto.OrderResultResponse> checkPaymentResult(
            @PathVariable Long orderId) {

        PaymentResultDto.OrderResultResponse response = orderService.checkPaymentResult(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 4️. 주문 취소
     *
     * - 특정 주문 ID를 취소 요청
     * - Mock API를 통해 주문 상태를 "CANCELED"로 변경
     * - 취소 로그를 DB에 기록
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(
        summary = "주문 취소",
        description = SwaggerTags.ORDER_CANCEL_DESC
    )
    public ResponseEntity<PaymentResultDto.OrderCancelResponse> cancelOrder(
            @PathVariable String orderId) {

        PaymentResultDto.OrderCancelResponse response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }
}
