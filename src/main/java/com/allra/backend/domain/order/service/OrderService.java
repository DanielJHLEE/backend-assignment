package com.allra.backend.domain.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.repository.CartRepository;
import com.allra.backend.domain.order.entity.OrderEntity;
import com.allra.backend.domain.order.entity.OrderItemEntity;
import com.allra.backend.domain.order.entity.OrderStatus;
import com.allra.backend.domain.payment.dto.PaymentResultDto;
import com.allra.backend.domain.payment.service.PaymentService;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.repository.ProductRepository;
import com.allra.backend.domain.user.entity.UserEntity;
import com.allra.backend.domain.order.repository.OrderRepository;

import com.allra.backend.global.exception.BusinessException;
import com.allra.backend.global.util.MockIdUtil;

import lombok.RequiredArgsConstructor;

/**
 * OrderService
 *
 * 주문 생성 → 결제 요청 → 결제 결과 / 취소 로직 담당
 * ---------------------------------------------------------
 * 1️. 주문 생성 : 장바구니 기반으로 Mock API에 주문 생성 요청
 * 2️. 결제 요청 : 주문 ID 기반 결제 요청 → 상태별 후속 처리
 *    - SUCCESS  : 재고 차감 + 장바구니 비움
 *    - FAILED   : 재고 복원
 *    - CANCELLED: 재고 복원
 *    - PENDING  : 대기 상태
 * ---------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    /**
     * 1. 주문 생성
     * - 사용자의 장바구니를 기반으로 주문 생성
     * - ProductEntity, OrderEntity 내부 비즈니스 로직 적극 활용
     * - Mock API에 주문 생성 요청 및 결제 로그 저장
     */
    @Transactional
    public PaymentResultDto.OrderCreateResponse createOrder(Long userId) {
        // 1️. 사용자 장바구니 조회
        List<CartEntity> carts = cartRepository.findUserCartsByUserId(userId);
        if (carts.isEmpty()) {
            throw new BusinessException("장바구니가 비어 있습니다.");
        }

        // 2️. 장바구니 내 상품 아이템 펼치기
        List<CartItemEntity> cartItems = carts.stream()
                .flatMap(cart -> cart.getItems().stream())
                .toList();

        if (cartItems.isEmpty()) {
            throw new BusinessException("장바구니에 상품이 없습니다.");
        }

        // 3️. 주문 기본 엔티티 생성 (status 기본값: CREATED)
        UserEntity user = UserEntity.builder().id(userId).build();
        OrderEntity orderEntity = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .build();

        // 4️. 장바구니 상품 → 주문 상세로 변환
        for (CartItemEntity cartItem : cartItems) {
            ProductEntity product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new BusinessException("상품을 찾을 수 없습니다."));

            // 재고 검증 (ProductEntity의 도메인 로직 사용)
            product.validateStock(cartItem.getQuantity());

            // 주문 상세 생성 (OrderItemEntity와 OrderEntity의 관계 설정)
            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .product(product)
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            //  양방향 관계 자동 연결
            orderEntity.addItem(orderItem);
        }

        // 5️. 주문 총 금액 계산 (OrderEntity의 내부 로직 사용)
        orderEntity.calculateTotalPrice();

        // 6️. DB 저장 (Order + OrderItem cascade 저장)
        orderRepository.save(orderEntity);

        // 7️. Mock API 호출 → 결제 로그 저장 (PaymentLogEntity)
        return paymentService.createOrder(
                userId,
                orderEntity.getItems().stream()
                        .map(OrderItemEntity::getProduct)
                        .toList(),
                orderEntity.getTotalPrice(),
                orderEntity
        );
    }

    /**
     * 2. 결제 요청
     * - 생성된 주문 ID를 기반으로 Mock 결제 API 호출
     * - 결제 성공/실패/취소 상태에 따라 재고 및 장바구니 처리
     */
    @Transactional
    public PaymentResultDto.OrderResultResponse processPayment(String orderId) {
        // 1.cartService MockIdUtil로 변환 (String → Long)
        Long dbOrderId = MockIdUtil.toEntityId(orderId);

        // 2. 주문 조회
        OrderEntity order = orderRepository.findById(dbOrderId)
                .orElseThrow(() -> new BusinessException("해당 주문을 찾을 수 없습니다."));

        // 3. 결제 금액 조회
        int amount = order.getTotalPrice();

        // 4. Mock API 결제 요청 (여긴 String 그대로 넘겨야 함)
        PaymentResultDto.OrderResultResponse paymentResponse =
                paymentService.processPayment(orderId, amount);

        // 5. 결제 상태별 처리
        String status = paymentResponse.getStatus().toUpperCase();

        switch (status) {
            case "SUCCESS" -> handleSuccess(order);   // 재고 차감 + 장바구니 비움
            case "FAILED", "CANCELED" -> handleRollback(order); // 재고 복원
            default -> order.updateStatus(OrderStatus.PENDING);
        }

        // 6. 최종 주문 상태 저장
        orderRepository.save(order);

        return paymentResponse;
    }

    /** 2-1 결제 성공 시 : 재고 차감 + 장바구니 비움 */
    private void handleSuccess(OrderEntity order) {
        // 1. 상품 재고 차감
        for (OrderItemEntity item : order.getItems()) {
            ProductEntity product = item.getProduct();
            int newStock = product.getStock() - item.getQuantity();
            if (newStock < 0) {
                throw new BusinessException("상품 재고가 부족합니다: " + product.getName());
            }
            product.setStock(newStock);
            product.setSoldOut(newStock == 0);
        }

        // 2. 장바구니 비우기
        cartRepository.deleteAllByUserId(order.getUser().getId());

        // 3. 주문 상태 갱신
        order.updateStatus(OrderStatus.SUCCESS);
    }

    /** 2-2 결제 실패 / 취소 시 : 재고 복원 */
    private void handleRollback(OrderEntity order) {
        for (OrderItemEntity item : order.getItems()) {
            ProductEntity product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            product.setSoldOut(false);
        }

        order.updateStatus(OrderStatus.FAILED);
    }

    /** 3. 결제 결과 조회 */
    public PaymentResultDto.OrderResultResponse checkPaymentResult(Long orderId) {
        return paymentService.checkPaymentResult(orderId);
    }

    /** 4. 주문 취소 요청 */
    public PaymentResultDto.OrderCancelResponse cancelOrder(String orderId) {
        return paymentService.cancelOrder(orderId);
    }

}
