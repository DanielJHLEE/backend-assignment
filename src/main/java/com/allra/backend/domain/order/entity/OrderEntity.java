package com.allra.backend.domain.order.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.allra.backend.domain.user.entity.UserEntity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 주문 엔티티 (OrderEntity)
 * 
 * 사용자 주문의 기본 정보를 관리합니다.
 * - 주문자 정보, 총 주문 금액, 상태, 주문 일시, 주문 상세 목록을 포함합니다.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    /** 주문 ID (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 주문자 (FK: user.id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** Mock API용 주문번호 (ORD_20251110_053149_194252 형식) */
    @Column(name = "mock_order_id", unique = true)
    private String mockOrderId;

    /** 총 주문 금액 */
    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    /** 주문 상태 (EnumType.STRING) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    /** 주문 일시 (기본값: LocalDateTime.now()) */
    @Column(name = "order_date", nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    /** 주문 상세 목록 (1:N 관계, cascade 삭제 포함) */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    // ==============================
    // 🧩 비즈니스 로직
    // ==============================

    /**
     * 주문 상품 추가
     * @param item 주문 상세 항목
     */
    public void addItem(OrderItemEntity item) {
        this.items.add(item);
        item.setOrder(this);
    }

    /**
     * 총 금액 계산
     * - 각 아이템의 (단가 × 수량)을 합산하여 totalPrice에 반영
     */
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToInt(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    /**
     * 주문 상태 변경
     * @param newStatus 새로운 주문 상태
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }
}
