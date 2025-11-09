package com.allra.backend.domain.order;

import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.repository.CartRepository;
import com.allra.backend.domain.order.entity.OrderEntity;
import com.allra.backend.domain.order.repository.OrderRepository;
import com.allra.backend.domain.order.service.OrderService;
import com.allra.backend.domain.payment.dto.PaymentResultDto;
import com.allra.backend.domain.payment.service.PaymentService;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.repository.ProductRepository;
import com.allra.backend.domain.user.entity.UserEntity;
import com.allra.backend.global.exception.BusinessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

/**
 * ✅ OrderServiceTest
 *
 * 실제 DB 접근 없이 Repository, Service를 Mock으로 대체하여
 * 주문 생성 및 예외 케이스를 단위 테스트합니다.
 */
public class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("🟢 주문 생성 성공 - 장바구니 기반 정상 주문 흐름 검증")
    void createOrder_success() {
        // given
        Long userId = 1L;

        UserEntity user = UserEntity.builder().id(userId).name("이재홍").email("jhlee@example.com").build();
        ProductEntity product = ProductEntity.builder()
                .id(10L).name("테스트상품").price(10000).stock(10).soldOut(false).build();

        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        CartEntity cart = new CartEntity();
        cart.setUser(user);
        cart.setItems(List.of(cartItem));

        when(cartRepository.findUserCartsByUserId(userId)).thenReturn(List.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentService.createOrder(any(), any(), anyInt(), any(OrderEntity.class)))
                .thenReturn(PaymentResultDto.OrderCreateResponse.builder()
                        .status("PENDING")
                        .message("Mock Payment Initialized")
                        .build());

        // when
        PaymentResultDto.OrderCreateResponse result = orderService.createOrder(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(paymentService, times(1)).createOrder(any(), any(), anyInt(), any(OrderEntity.class));
    }

    @Test
    @DisplayName("🔴 장바구니가 비어있을 경우 BusinessException 발생")
    void createOrder_whenCartEmpty_shouldThrowException() {
        when(cartRepository.findUserCartsByUserId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.createOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("장바구니가 비어 있습니다");

        verify(cartRepository, times(1)).findUserCartsByUserId(1L);
    }

    @Test
    @DisplayName("🔴 장바구니는 있지만 상품이 존재하지 않을 경우 BusinessException 발생")
    void createOrder_whenProductNotFound_shouldThrowException() {
        Long userId = 1L;
        ProductEntity product = ProductEntity.builder().id(99L).name("테스트상품").build();
        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        CartEntity cart = new CartEntity();
        cart.setItems(List.of(cartItem));

        when(cartRepository.findUserCartsByUserId(userId)).thenReturn(List.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }
}
