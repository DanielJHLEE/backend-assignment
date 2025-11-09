package com.allra.backend.domain.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.allra.backend.domain.payment.entity.PaymentLogEntity;

/**
 * PaymentLogRepository
 *
 * 결제 로그(PaymentLogEntity)를 관리하는 Repository.
 * - 모든 결제 요청/응답 내역을 DB에 저장 및 조회.
 * - JpaRepository를 상속받아 기본 CRUD 기능 자동 제공.
 */
@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLogEntity, Long> {

    /**
     * 특정 주문 ID로 로그 전체 조회
     */
    List<PaymentLogEntity> findByOrderId(Long orderId);

    /**
     * 특정 주문의 가장 최근 결제 로그 1건 조회
     * - 결제 취소 시 금액으로 사용
     */
    @Query(
        value = "SELECT * FROM payment_log WHERE order_id = :orderId ORDER BY created_at DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<PaymentLogEntity> findLatestByOrderId(Long orderId);

}
