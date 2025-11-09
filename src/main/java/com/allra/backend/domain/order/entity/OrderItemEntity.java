package com.allra.backend.domain.order.entity;

import com.allra.backend.domain.product.entity.ProductEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

/**
 * 주문 상세 엔티티 (OrderItemEntity)
 * 
 * 주문에 포함된 개별 상품의 정보를 관리합니다.
 * - 상품 ID, 주문 수량, 단가, 주문 관계를 포함합니다.
 */
@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    /** 주문 상세 ID (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 주문 (FK: orders.id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    /** 상품 (FK: product.id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /** 주문 수량 */
    @Column(nullable = false)
    private int quantity;

    /** 단가 (주문 시점의 상품 가격 스냅샷) */
    @Column(nullable = false)
    private int price;
}
