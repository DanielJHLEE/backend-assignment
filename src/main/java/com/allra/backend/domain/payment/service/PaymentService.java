package com.allra.backend.domain.payment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.allra.backend.domain.mockapi.dto.MockApiCancelDto;
import com.allra.backend.domain.mockapi.dto.MockApiOrderDto;
import com.allra.backend.domain.mockapi.dto.MockApiPaymentDto;
import com.allra.backend.domain.order.entity.OrderEntity;
import com.allra.backend.domain.order.entity.OrderStatus;
import com.allra.backend.domain.payment.dto.PaymentResultDto;
import com.allra.backend.domain.payment.entity.PaymentLogEntity;
import com.allra.backend.domain.payment.repository.PaymentLogRepository;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.global.exception.BusinessException;
import com.allra.backend.global.util.MockIdUtil;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * PaymentService (로그 기반)
 * 
 * - Mock API를 호출하고, 모든 요청/응답 내역을 PaymentLogEntity에 저장.
 * - Mock 응답의 orderId, transactionId 그대로 기록.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentLogRepository paymentLogRepository;

    @Value("${MOCK_BASE_URL:${mock.api.base-url:http://localhost:8080}}")
    private String baseUrl;
    private WebClient webClient;

    @PostConstruct
    private void initWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

   /** 1. 주문 생성 */
	@Transactional
	public PaymentResultDto.OrderCreateResponse createOrder(
			Long userId, List<ProductEntity> products, int amount, OrderEntity orderEntity) {

		// 1️. Mock 요청 객체 구성 (다중 상품 기반)
		MockApiOrderDto.MockOrderCreateRequestDto mockRequest =
				new MockApiOrderDto.MockOrderCreateRequestDto(userId, products, (long) amount);

		// 2️. Mock API 호출 (주문 생성 요청)
		MockApiOrderDto.MockOrderCreateResponseDto mockResponse = webClient.post()
				.uri("/api/mock/order")
				.bodyValue(mockRequest)
				.retrieve()
				.bodyToMono(MockApiOrderDto.MockOrderCreateResponseDto.class)
				.block();

		// 2-1 응답 유효성 검증 (예외 처리 통일)
		if (mockResponse == null) {
			throw new BusinessException("Mock 주문 생성 API 응답이 없습니다.");
		}

		// 3️. 로그 저장 — 실제 OrderEntity를 FK로 사용
		PaymentLogEntity log = PaymentLogEntity.builder()
				.order(orderEntity) // 실제 DB 주문 엔티티 연결
				.transactionId(mockResponse.getOrderId()) // Mock API 주문번호를 트랜잭션 ID로 저장
				.amount(amount)
				.status(mockResponse.getStatus())   // CREATED
				.message(mockResponse.getMessage()) // 성공 메시지
				.createdAt(LocalDateTime.now())
				.build();

		paymentLogRepository.save(log);

		// 4️. 주문 상태 동기화 (Mock 응답 상태 반영)
		orderEntity.updateStatus(OrderStatus.CREATED);

		// 5️. 응답 DTO 반환
		return PaymentResultDto.OrderCreateResponse.builder()
				.orderId(mockResponse.getOrderId()) // Mock 주문번호 그대로 반환
				.status(mockResponse.getStatus())
				.message(mockResponse.getMessage())
				.build();
	}

    /** 2. 결제 요청 (1단계: PENDING 상태) */
    @Transactional
    public PaymentResultDto.OrderResultResponse processPayment(String orderId, int amount) {
        MockApiPaymentDto.MockPayRequest mockRequest = new MockApiPaymentDto.MockPayRequest(orderId, amount);

        MockApiPaymentDto.MockPayResponse mockResponse = webClient.post()
                .uri("/api/mock/payment")
                .bodyValue(mockRequest)
                .retrieve()
                .bodyToMono(MockApiPaymentDto.MockPayResponse.class)
                .block();

		// Mock API에서 받은 orderId("ORD_...") → 실제 DB용 Long ID로 변환
		Long dbOrderId = MockIdUtil.toEntityId(orderId);

        // 결제 요청 직후 — PENDING 상태 저장
        PaymentLogEntity pendingLog = PaymentLogEntity.builder()
                .order(OrderEntity.builder().id(dbOrderId).build())
                .transactionId(mockResponse.getTransactionId())
                .amount(amount)
                .status(mockResponse.getStatus()) // 항상 "PENDING" -> 비동기로 "SUCCESS" / "FAILED" 업데이트됨.
                .message(mockResponse.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        // DB결제이력 저장
        paymentLogRepository.save(pendingLog);

        return PaymentResultDto.OrderResultResponse.builder()
                .status(mockResponse.getStatus())
                .transactionId(mockResponse.getTransactionId())
                .message(mockResponse.getMessage())
                .build();
    }

	/**
	 * 3. 결제 결과 조회 및 DB 상태 업데이트
	 *
	 * - Mock 결제 API(`/api/mock/payment/result/{orderId}`) 호출
	 * - Mock 메모리에 저장된 최신 결제 상태를 조회하고,
	 *   그 결과를 DB(Order, PaymentLog)에 동기화한다.
	 */
	@Transactional
	public PaymentResultDto.OrderResultResponse checkPaymentResult(Long orderId) {
		// 1. Mock API 호출 (현재 결제 상태 조회)
		MockApiPaymentDto.MockPayResponse mockRes = webClient.get()
				.uri("/api/mock/payment/result/{orderId}", orderId)
				.retrieve()
				.bodyToMono(MockApiPaymentDto.MockPayResponse.class)
				.block();

		if (mockRes == null || mockRes.getStatus() == null) {
			throw new BusinessException("Mock 결제 상태 조회에 실패했습니다. (orderId=" + orderId + ")");
		}

		// 2. 최신 결제 로그 조회
		PaymentLogEntity latestLog = paymentLogRepository.findLatestByOrderId(orderId)
				.orElseThrow(() -> new BusinessException("결제 로그가 존재하지 않습니다. (orderId=" + orderId + ")"));

		// 3. 주문 엔티티 조회
		OrderEntity order = latestLog.getOrder();

		// 4. Mock 응답 상태를 DB에 동기화
		latestLog.setStatus(mockRes.getStatus());
		latestLog.setMessage(mockRes.getMessage());
		latestLog.setUpdateAt(LocalDateTime.now());

		switch (mockRes.getStatus().toUpperCase()) {
			case "SUCCESS" -> order.updateStatus(OrderStatus.SUCCESS);
			case "FAILED" -> order.updateStatus(OrderStatus.FAILED);
			default -> order.updateStatus(OrderStatus.PENDING);
		}

		// 5. 저장 (로그 + 주문 상태 동기화)
		paymentLogRepository.save(latestLog);

		// 6. 결과 DTO 반환
		return PaymentResultDto.OrderResultResponse.builder()
				.status(mockRes.getStatus())
				.transactionId(mockRes.getTransactionId())
				.message(mockRes.getMessage())
				.build();
	}

    /** 4. 주문 취소 */
	@Transactional
	public PaymentResultDto.OrderCancelResponse cancelOrder(String orderId) {
		// 1. Mock ID → DB용 Long ID 변환
		Long dbOrderId = MockIdUtil.toEntityId(orderId);

		// 2. 기존 결제 금액 조회 (가장 최근 결제 로그)
		PaymentLogEntity lastLog = paymentLogRepository
				.findLatestByOrderId(dbOrderId) // Long으로 변환
				.orElseThrow(() -> new BusinessException("해당 주문의 결제 내역을 찾을 수 없습니다."));

		// 3. Mock API에 취소 요청
		MockApiCancelDto.MockCancelRequest mockRequest = new MockApiCancelDto.MockCancelRequest(orderId);
		MockApiCancelDto.MockCancelResponse mockResponse = webClient.post()
				.uri("/api/mock/order/cancel")
				.bodyValue(mockRequest)
				.retrieve()
				.bodyToMono(MockApiCancelDto.MockCancelResponse.class)
				.block();

		if (mockResponse == null || mockResponse.getStatus() == null) {
			throw new BusinessException("Mock 주문 취소 API 응답이 유효하지 않습니다.");
		}

		// 4. 취소 로그 저장 — 기존 결제 금액 그대로 유지
		PaymentLogEntity cancelLog = PaymentLogEntity.builder()
				.order(lastLog.getOrder()) // FK 그대로 유지
				.transactionId("CANCEL-" + orderId)
				.amount(lastLog.getAmount())
				.status(mockResponse.getStatus()) // "CANCELED"
				.message(mockResponse.getMessage())
				.createdAt(LocalDateTime.now())
				.build();

		paymentLogRepository.save(cancelLog);

		// 5. 주문 상태도 동기화 (OrderEntity에 반영)
		OrderEntity order = lastLog.getOrder();
		order.updateStatus(OrderStatus.CANCELED);

		// 6. 응답 DTO 반환
		return PaymentResultDto.OrderCancelResponse.builder()
				.status(mockResponse.getStatus())
				.message(mockResponse.getMessage())
				.build();
	}


}
