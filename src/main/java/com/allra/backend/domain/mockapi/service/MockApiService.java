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
    private final Map<String, String> paymentStatusMap = new ConcurrentHashMap<>();

    /**
     * 주문 생성 Mock
     * - userId, products(여러 상품)를 받아 주문번호를 자동 생성
     * - 상태값: CREATED
     * - 필수 검증: userId, products 리스트
     */
    public MockApiOrderDto.MockOrderCreateResponseDto createOrder(MockApiOrderDto.MockOrderCreateRequestDto request) {

        // 1️. 유저 ID 검증
        if (request.getUserId() == null || request.getUserId() <= 0) {
            return MockApiOrderDto.MockOrderCreateResponseDto.builder()
                    .orderId(null)
                    .status("INVALID_USER")
                    .message("Invalid userId. Order cannot be created.")
                    .build();
        }

        // 2️. 상품 리스트 검증
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            return MockApiOrderDto.MockOrderCreateResponseDto.builder()
                    .orderId(null)
                    .status("INVALID_PRODUCT_LIST")
                    .message("No products provided. At least one product is required.")
                    .build();
        }

        // 3️. 주문 생성 처리
        String orderId = generateOrderId();
        int productCount = request.getProducts().size();

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
     * - 8~15초 후 내부적으로 SUCCESS / FAILED 로 자동 전환 (비동기)
     * - 주문번호나 금액이 유효하지 않으면 즉시 실패 처리
     */
    public MockApiPaymentDto.MockPayResponse processPayment(MockApiPaymentDto.MockPayRequest request) {

        String orderId = request.getOrderId();
        int amount = request.getAmount();

        // 1️. 주문번호(orderId) 검증
        if (orderId == null || orderId.isBlank()) {
            return MockApiPaymentDto.MockPayResponse.builder()
                    .status("INVALID_ORDER_ID")
                    .transactionId(generateTransactionId())
                    .message("Invalid orderId. Payment cannot be processed.")
                    .build();
        }

        // 2️. 결제 금액 검증 (0 이하 금액은 즉시 실패)
        if (amount <= 0) {
            return MockApiPaymentDto.MockPayResponse.builder()
                    .status("FAILED")
                    .transactionId(generateTransactionId())
                    .message(String.format("Payment failed immediately: invalid amount (%d)", amount))
                    .build();
        }

        // 3️. 최초 요청 시: PENDING 상태 저장
        paymentStatusMap.put(orderId, "PENDING");

        // 4️. 비동기적으로 SUCCESS / FAILED 상태 전환
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep((long) (Math.random() * 7000) + 8000); // 8~15초 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            boolean isSuccess = Math.random() < 0.8; // 80% 성공 확률
            String result = isSuccess ? "SUCCESS" : "FAILED";
            paymentStatusMap.put(orderId, result);
        });

        // 5️. 즉시 응답: 현재는 처리 중(PENDING)
        return MockApiPaymentDto.MockPayResponse.builder()
                .status("PENDING")
                .transactionId(generateTransactionId())
                .message(String.format("Payment request for orderId=%s received. Processing...", orderId))
                .build();
    }


    /**
     * 결제 결과 조회 Mock
     * - 현재 결제 상태를 확인할 수 있음.
     * - PENDING / SUCCESS / FAILED 중 하나 반환.
     */
    public MockApiPaymentDto.MockPayResponse getPaymentResult(String orderId) {
        String status = paymentStatusMap.getOrDefault(orderId, "NOT_FOUND");

        return MockApiPaymentDto.MockPayResponse.builder()
                .status(status)
                .transactionId("txn_" + orderId)
                .message(String.format("Payment status for orderId=%s is %s", orderId, status))
                .build();
    }

    /**
     * 주문 취소 Mock
     * - 단순히 취소 상태를 반환
     * - 상태값: CANCELED
     */
    public MockApiCancelDto.MockCancelResponse cancelOrder(MockApiCancelDto.MockCancelRequest request) {
        String orderId = request.getOrderId();

         // 1️. 현재 결제 상태 조회
        String currentStatus = paymentStatusMap.get(orderId);

        // 2️. 주문이 존재하지 않으면 NOT_FOUND 반환
        if (currentStatus == null) {
            return MockApiCancelDto.MockCancelResponse.builder()
                    .status("NOT_FOUND")
                    .message(String.format("OrderId=%s not found. Cancel request ignored.", orderId))
                    .build();
        }

        // 3️. 결제 실패(FAILED) 상태는 취소 불가 처리
        if ("FAILED".equalsIgnoreCase(currentStatus)) {
            return MockApiCancelDto.MockCancelResponse.builder()
                    .status("CANNOT_CANCEL")
                    .message(String.format("OrderId=%s cannot be canceled because payment has already failed.", orderId))
                    .build();
        }

        // 4️. 결제 성공(SUCCESS) 또는 진행중(PENDING) 상태는 취소 가능
        paymentStatusMap.put(orderId, "CANCELED");

        return MockApiCancelDto.MockCancelResponse.builder()
                .status("CANCELED")
                .message(String.format("Order canceled successfully for orderId=%s", orderId))
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
