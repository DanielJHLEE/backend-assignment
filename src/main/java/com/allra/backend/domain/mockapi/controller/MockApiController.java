package com.allra.backend.domain.mockapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allra.backend.domain.mockapi.dto.MockApiOrderDto;
import com.allra.backend.docs.swagger.SwaggerTags;
import com.allra.backend.domain.mockapi.dto.MockApiCancelDto;
import com.allra.backend.domain.mockapi.dto.MockApiPaymentDto;

import com.allra.backend.domain.mockapi.service.MockApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * MockApiController
 * 
 * 외부 PG사 및 주문 서버를 시뮬레이션하는 Mock API 컨트롤러입니다.
 * 실제 결제·주문·취소 플로우를 모사하여 테스트용으로 동작합니다.
 */
@RestController
@RequestMapping("/api/mock")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.MOCK_NAME, description = SwaggerTags.MOCK_DESC)
public class MockApiController {

    private final MockApiService mockApiService;

    /** 
     * 결제 결과 조회 Mock API (PENDING / SUCCESS / FAILED 조회용) */
    @GetMapping("/payment/result/{orderId}")
    @Operation(
        summary = "Mock 결제 상태 조회",
        description = SwaggerTags.MOCK_PAYMENT_RESULT_DESC
    )
    public ResponseEntity<MockApiPaymentDto.MockPayResponse> getPaymentResult(@PathVariable Long orderId) {
        MockApiPaymentDto.MockPayResponse response = mockApiService.getPaymentResult(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 생성 Mock API
     * - OrderStatus: CREATED
     */
    @PostMapping("/order")
    @Operation(
        summary = "Mock 주문 생성",
        description = SwaggerTags.MOCK_ORDER_CREATE_DESC
    )
    public ResponseEntity<MockApiOrderDto.MockOrderCreateResponseDto> createOrder(
            @RequestBody MockApiOrderDto.MockOrderCreateRequestDto request) {
        MockApiOrderDto.MockOrderCreateResponseDto response = mockApiService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 처리 Mock API
     * - amount > 0 → SUCCESS (OrderStatus.COMPLETED)
     * - amount ≤ 0 → FAILED (OrderStatus.FAILED)
     */
    @PostMapping("/payment")
    @Operation(
        summary = "Mock 결제 요청",
        description = SwaggerTags.MOCK_PAYMENT_DESC
    )
    public ResponseEntity<MockApiPaymentDto.MockPayResponse> processPayment(@RequestBody MockApiPaymentDto.MockPayRequest request) {
        MockApiPaymentDto.MockPayResponse response = mockApiService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 취소 Mock API
     * - OrderStatus: CANCELED
     */
    @PostMapping("/order/cancel")
    @Operation(
        summary = "Mock 주문 취소",
        description = SwaggerTags.MOCK_ORDER_CANCEL_DESC
    )
    public ResponseEntity<MockApiCancelDto.MockCancelResponse> cancelOrder(@RequestBody MockApiCancelDto.MockCancelRequest request) {
        MockApiCancelDto.MockCancelResponse response = mockApiService.cancelOrder(request);
        return ResponseEntity.ok(response);
    }
}
