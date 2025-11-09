package com.allra.backend.domain.mockapi.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.allra.backend.domain.mockapi.dto.MockApiOrderDto;
import com.allra.backend.domain.mockapi.dto.MockApiPaymentDto;
import com.allra.backend.domain.mockapi.dto.MockApiCancelDto;

/**
 * MockService
 *
 * 외부 PG사 및 주문 시스템을 흉내내는 Mock 서비스 클래스.
 * 결제 요청 시 PENDING(처리중) 상태를 반환하고,
 * 일정 시간 후 SUCCESS 또는 FAILED 로 자동 변경.
 */
@Service
public class MockApiService {

    /** 결제 상태를 임시 보관하는 메모리 저장소 */
    private final Map<Long, String> paymentStatusMap = new ConcurrentHashMap<>();

    /**
     * 결제 결과 조회 Mock
     * - 현재 결제 상태를 확인할 수 있음.
     * - PENDING / SUCCESS / FAILED 중 하나 반환.
     */
    public MockApiPaymentDto.MockPayResponse getPaymentResult(Long orderId) {
        String status = paymentStatusMap.getOrDefault(orderId, "NOT_FOUND");

        return MockApiPaymentDto.MockPayResponse.builder()
                .status(status)
                .transactionId("txn_" + orderId)
                .message(String.format("Payment status for orderId=%d is %s", orderId, status))
                .build();
    }

    /**
     * 주문 생성 Mock
     * - userId, products(여러 상품)를 받아 주문번호를 자동 생성
     * - 상태값: CREATED
     */
    public MockApiOrderDto.MockOrderCreateResponseDto createOrder(MockApiOrderDto.MockOrderCreateRequestDto request) {
        String orderId = generateOrderId();

        int productCount = (request.getProducts() != null) ? request.getProducts().size() : 0;

        String message = String.format(
                "Order created successfully for userId=%d with %d product(s). Total amount: %d",
                request.getUserId(), productCount, request.getAmount()
        );

        return MockApiOrderDto.MockOrderCreateResponseDto.builder()
                .orderId(orderId)
                .status("CREATED")
                .message(message)
                .build();
    }

    /**
     * 결제 처리 Mock
     *
     * - 결제 요청 시: PENDING 상태 즉시 반환
     * - 2~4초 후 내부적으로 SUCCESS / FAILED 로 변경 (비동기)
     */
    public MockApiPaymentDto.MockPayResponse processPayment(MockApiPaymentDto.MockPayRequest request) {
        Long orderId = request.getOrderId();

        // 1. 최초 요청 시 무조건 PENDING 저장
        paymentStatusMap.put(orderId, "PENDING");

        // 2. 비동기적으로 SUCCESS / FAILED 전환
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep((long) (Math.random() * 7000) + 8000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            boolean isSuccess = Math.random() < 0.8; // 80%성공, 20%실패
            String result = (request.getAmount() > 0 && isSuccess) ? "SUCCESS" : "FAILED";
            paymentStatusMap.put(orderId, result);
        });

        // 3️. 즉시 PENDING 상태 응답
        return MockApiPaymentDto.MockPayResponse.builder()
                .status("PENDING")
                .transactionId(generateTransactionId())
                .message(String.format(
                        "Payment request for orderId=%d received. Processing...",
                        orderId))
                .build();
    }

    /**
     * 주문 취소 Mock
     * - 단순히 취소 상태를 반환
     * - 상태값: CANCELED
     */
    public MockApiCancelDto.MockCancelResponse cancelOrder(MockApiCancelDto.MockCancelRequest request) {
        return MockApiCancelDto.MockCancelResponse.builder()
                .status("CANCELED")
                .message(String.format("Order canceled successfully for orderId=%d", request.getOrderId()))
                .build();
    }

    // ============================================================
    // 내부 유틸 메서드 (고유 ID 생성)
    // ============================================================

    private String generateOrderId() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        int random = (int) (Math.random() * 900000) + 100000; // 6자리 난수
        return "ORD_" + date + "_" + random;
    }

    private String generateTransactionId() {
        return "txn_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
