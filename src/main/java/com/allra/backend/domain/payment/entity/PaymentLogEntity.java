package com.allra.backend.domain.payment.entity;

import java.time.LocalDateTime;

import com.allra.backend.domain.order.entity.OrderEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 결제 로그 엔티티 (PaymentLogEntity)
 *
 * 외부 결제 API 호출 결과 및 응답 내역을 저장합니다.
 * - 주문 ID, 트랜잭션 ID, 결제 금액, 상태, 메시지, 요청 시간 포함
 */
@Entity
@Table(name = "payment_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLogEntity {

    /** 결제 로그 ID (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 주문 (FK: orders.id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    /** 외부 결제 트랜잭션 ID (예: PG사 결제 ID) */
    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId;

    /** 결제 금액 */
    @Column(name = "amount", nullable = false)
    private int amount;

    /** 결제 상태 (예: SUCCESS, FAIL, PENDING 등) */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** 결제 응답 메시지 (승인 결과 또는 오류 사유 등) */
    @Column(name = "message", length = 255)
    private String message;

    /** 결제 요청 시각 */
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 결제 수정 시각 */
    @Builder.Default
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

}
