package com.allra.backend.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.allra.backend.domain.order.entity.OrderEntity;

/**
 * OrderRepository
 * 
 * 주문 테이블(orders)에 대한 JPA 데이터 접근 계층.
 * 
 * <p>
 * - JpaRepository<OrderEntity, Long> 을 상속받아 기본 CRUD 메서드 제공  
 *   (save, findById, findAll, deleteById 등)  
 * - 별도의 구현 없이 즉시 사용 가능  
 * - 향후 주문 조회 조건 (예: userId로 조회 등)이 필요할 때  
 *   커스텀 메서드를 추가로 정의할 수 있음.
 * </p>
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // 예시) 특정 사용자의 주문 목록 조회 (필요시 사용)
    // List<OrderEntity> findByUserId(Long userId);
}
