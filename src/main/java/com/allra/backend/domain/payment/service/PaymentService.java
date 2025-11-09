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
import com.allra.backend.domain.order.repository.OrderRepository;
import com.allra.backend.domain.payment.dto.PaymentResultDto;
import com.allra.backend.domain.payment.entity.PaymentLogEntity;
import com.allra.backend.domain.payment.exception.PaymentErrorCode;
import com.allra.backend.domain.payment.exception.PaymentException;
import com.allra.backend.domain.payment.repository.PaymentLogRepository;
import com.allra.backend.domain.product.entity.ProductEntity;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PaymentService (로그 기반)
 * 
 * - Mock API를 호출하고, 모든 요청/응답 내역을 PaymentLogEntity에 저장.
 * - Mock 응답의 orderId, transactionId 그대로 기록.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
	private final PaymentLogRepository paymentLogRepository;
	private final OrderRepository orderRepository;
    
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
			throw new PaymentException(PaymentErrorCode.MOCK_API_RESPONSE_NULL);
		}

		// 2-2. 여기에 추가
		orderEntity.setMockOrderId(mockResponse.getOrderId());

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
		// 1️. 결제 요청 DTO 생성
		MockApiPaymentDto.MockPayRequest mockRequest = new MockApiPaymentDto.MockPayRequest(orderId, amount);

		MockApiPaymentDto.MockPayResponse mockResponse;

		try {
			// 2️. WebClient로 내부 Mock API 호출
			mockResponse = webClient.post()
					.uri("/api/mock/payment")
					.bodyValue(mockRequest)
					.retrieve()
					.bodyToMono(MockApiPaymentDto.MockPayResponse.class)
					.block();

			// 3️. 응답 검증 (null 응답 방지)
			if (mockResponse == null || mockResponse.getStatus() == null) {
				throw new PaymentException(PaymentErrorCode.MOCK_API_RESPONSE_NULL);
			}

		} catch (Exception e) {
			// 4️. 네트워크, 직렬화, 내부 오류 발생 시 예외 처리
			log.error("[PaymentService] 결제 요청 실패 - orderId={}, error={}", orderId, e.getMessage());
			throw new PaymentException(PaymentErrorCode.MOCK_PAYMENT_FAILED);
		}
		
		// 5. DB에서 주문 엔티티 조회 (mockOrderId 기반)
		OrderEntity orderEntity = orderRepository.findByMockOrderId(orderId)
				.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND));

		// 6. 결제 로그 저장
		PaymentLogEntity pendingLog = PaymentLogEntity.builder()
		 		.order(orderEntity) // FK 연결! (이거 빠지면 order_id null)
				.transactionId(mockResponse.getTransactionId())
				.amount(amount)
				.status(mockResponse.getStatus())
				.message(mockResponse.getMessage())
				.createdAt(LocalDateTime.now())
				.build();

		paymentLogRepository.save(pendingLog);

		// 76. 결제 결과 반환
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
	public PaymentResultDto.OrderResultResponse checkPaymentResult(String orderId) {
		// 1. Mock API 호출 (현재 결제 상태 조회)
		MockApiPaymentDto.MockPayResponse mockResponse = webClient.get()
				.uri("/api/mock/payment/result/{orderId}", orderId)
				.retrieve()
				.bodyToMono(MockApiPaymentDto.MockPayResponse.class)
				.block();

		if (mockResponse == null || mockResponse.getStatus() == null) {
			throw new PaymentException(PaymentErrorCode.MOCK_API_RESPONSE_NULL);
		}

		 // 2. mockOrderId 기반으로 주문 조회
		OrderEntity orderEntity = orderRepository.findByMockOrderId(orderId)
				.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND));

		// 3. 최신 결제 로그 조회 (OrderEntity 기반)
		PaymentLogEntity latestLog = paymentLogRepository.findLatestByOrderId(orderEntity.getId())
				.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND));

		// 4. 주문 엔티티 조회
		OrderEntity order = latestLog.getOrder();

		// 5. Mock 응답 상태를 DB에 동기화
		latestLog.setStatus(mockResponse.getStatus());
		latestLog.setMessage(mockResponse.getMessage());
		latestLog.setUpdateAt(LocalDateTime.now());

		switch (mockResponse.getStatus().toUpperCase()) {
			case "SUCCESS" -> order.updateStatus(OrderStatus.SUCCESS);
			case "FAILED" -> order.updateStatus(OrderStatus.FAILED);
			default -> order.updateStatus(OrderStatus.PENDING);
		}

		// 6. 저장 (로그 + 주문 상태 동기화)
		paymentLogRepository.save(latestLog);

		// 7. 결과 DTO 반환
		return PaymentResultDto.OrderResultResponse.builder()
				.status(mockResponse.getStatus())
				.transactionId(mockResponse.getTransactionId())
				.message(mockResponse.getMessage())
				.build();
	}

    @Transactional
    public PaymentResultDto.OrderCancelResponse cancelOrder(String orderId) {
        // 1. mockOrderId 기반 주문 조회
        OrderEntity orderEntity = orderRepository.findByMockOrderId(orderId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.ORDER_NOT_FOUND));

        // 2. 최신 결제 로그 조회
        PaymentLogEntity lastLog = paymentLogRepository.findLatestByOrderId(orderEntity.getId())
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND));

        // 3. Mock API 취소 요청
        MockApiCancelDto.MockCancelRequest mockRequest = new MockApiCancelDto.MockCancelRequest(orderId);
        MockApiCancelDto.MockCancelResponse mockResponse = webClient.post()
                .uri("/api/mock/order/cancel")
                .bodyValue(mockRequest)
                .retrieve()
                .bodyToMono(MockApiCancelDto.MockCancelResponse.class)
                .block();

        if (mockResponse == null || mockResponse.getStatus() == null) {
            throw new PaymentException(PaymentErrorCode.CANCEL_API_FAILED);
        }

        // 4. 취소 로그 저장
        PaymentLogEntity cancelLog = PaymentLogEntity.builder()
                .order(orderEntity)
                .transactionId("CANCEL-" + orderId + "-" + System.currentTimeMillis())
                .amount(lastLog.getAmount())
                .status(mockResponse.getStatus())
                .message(mockResponse.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
        paymentLogRepository.save(cancelLog);

        // 5. 주문 상태 반영
        orderEntity.updateStatus(OrderStatus.CANCELED);

        // 6. 결과 반환
        return PaymentResultDto.OrderCancelResponse.builder()
                .status(mockResponse.getStatus())
                .message(mockResponse.getMessage())
                .build();
    }

}
