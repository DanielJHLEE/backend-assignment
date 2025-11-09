package com.allra.backend.domain.payment;

import com.allra.backend.domain.mockapi.dto.MockApiCancelDto;
import com.allra.backend.domain.mockapi.dto.MockApiOrderDto;
import com.allra.backend.domain.mockapi.dto.MockApiPaymentDto;
import com.allra.backend.domain.order.entity.OrderEntity;
import com.allra.backend.domain.order.entity.OrderStatus;
import com.allra.backend.domain.order.repository.OrderRepository;
import com.allra.backend.domain.payment.entity.PaymentLogEntity;
import com.allra.backend.domain.payment.exception.PaymentErrorCode;
import com.allra.backend.domain.payment.exception.PaymentException;
import com.allra.backend.domain.payment.repository.PaymentLogRepository;
import com.allra.backend.domain.payment.service.PaymentService;
import com.allra.backend.domain.product.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ PaymentServiceTest (WebClient + Repository 완전 Mock)
 *
 * WebClient 체이닝 Mock을 통해 실제 API 호출 없이 비즈니스 로직만 검증합니다.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PaymentServiceTest {

    @Mock private PaymentLogRepository paymentLogRepository;
    @Mock private OrderRepository orderRepository;

    @Mock private WebClient webClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(paymentService, "webClient", webClient);
    }

    @Test
    @DisplayName("🟢 Mock 주문 생성 성공 - 결제 로그 정상 저장")
    void createOrder_success() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);

        List<ProductEntity> products = List.of(ProductEntity.builder().id(1L).name("상품1").price(10000).build());
        MockApiOrderDto.MockOrderCreateResponseDto mockResponse = MockApiOrderDto.MockOrderCreateResponseDto.builder()
                .orderId("ORD_001").status("CREATED").message("Mock order created successfully").build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/mock/order")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MockApiOrderDto.MockOrderCreateResponseDto.class)).thenReturn(Mono.just(mockResponse));

        var result = paymentService.createOrder(1L, products, 10000, order);

        assertThat(result.getStatus()).isEqualTo("CREATED");
        verify(paymentLogRepository, times(1)).save(any(PaymentLogEntity.class));
    }

    @Test
    @DisplayName("🟢 결제 요청 성공 - SUCCESS 상태 반환 및 로그 저장")
    void processPayment_success() {
        String orderId = "ORD_123";
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        when(orderRepository.findByMockOrderId(orderId)).thenReturn(Optional.of(order));

        MockApiPaymentDto.MockPayResponse mockResponse = MockApiPaymentDto.MockPayResponse.builder()
                .status("SUCCESS").transactionId("TXN_999").message("Payment successful").build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/mock/payment")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MockApiPaymentDto.MockPayResponse.class)).thenReturn(Mono.just(mockResponse));

        var result = paymentService.processPayment(orderId, 50000);

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        verify(paymentLogRepository, times(1)).save(any(PaymentLogEntity.class));
    }

    @Test
    @DisplayName("🟢 결제 결과 조회 성공 - DB 상태 동기화 및 로그 갱신")
    void checkPaymentResult_success() {
        String orderId = "ORD_777";
        OrderEntity order = new OrderEntity();
        order.setId(1L);

        PaymentLogEntity lastLog = PaymentLogEntity.builder()
                .id(100L).order(order).status("PENDING").amount(10000)
                .createdAt(LocalDateTime.now()).build();

        MockApiPaymentDto.MockPayResponse mockResponse = MockApiPaymentDto.MockPayResponse.builder()
                .status("SUCCESS").transactionId("TXN_777").message("Payment completed").build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/mock/payment/result/{orderId}", orderId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MockApiPaymentDto.MockPayResponse.class)).thenReturn(Mono.just(mockResponse));

        when(orderRepository.findByMockOrderId(orderId)).thenReturn(Optional.of(order));
        when(paymentLogRepository.findLatestByOrderId(order.getId())).thenReturn(Optional.of(lastLog));

        var result = paymentService.checkPaymentResult(orderId);

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SUCCESS);
        verify(paymentLogRepository, times(1)).save(any(PaymentLogEntity.class));
    }

    @Test
    @DisplayName("🟢 주문 취소 성공 - 상태 CANCELED로 업데이트 및 로그 저장")
    void cancelOrder_success() {
        String orderId = "ORD_888";
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setStatus(OrderStatus.SUCCESS);

        PaymentLogEntity lastLog = PaymentLogEntity.builder()
                .id(50L).order(order).status("SUCCESS").amount(15000)
                .createdAt(LocalDateTime.now()).build();

        MockApiCancelDto.MockCancelResponse mockResponse = MockApiCancelDto.MockCancelResponse.builder()
                .status("CANCELED").message("Order canceled successfully").build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/mock/order/cancel")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MockApiCancelDto.MockCancelResponse.class)).thenReturn(Mono.just(mockResponse));

        when(orderRepository.findByMockOrderId(orderId)).thenReturn(Optional.of(order));
        when(paymentLogRepository.findLatestByOrderId(order.getId())).thenReturn(Optional.of(lastLog));

        var result = paymentService.cancelOrder(orderId);

        assertThat(result.getStatus()).isEqualTo("CANCELED");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(paymentLogRepository, times(1)).save(any(PaymentLogEntity.class));
    }

    @Test
    @DisplayName("🔴 Mock 응답이 null일 경우 PaymentException 발생")
    void checkPaymentResult_whenMockResponseNull_shouldThrowException() {
        String orderId = "ORD_FAIL";
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(orderId))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MockApiPaymentDto.MockPayResponse.class)).thenReturn(Mono.empty());

        assertThatThrownBy(() -> paymentService.checkPaymentResult(orderId))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining(PaymentErrorCode.MOCK_API_RESPONSE_NULL.getMessage());
    }
}
