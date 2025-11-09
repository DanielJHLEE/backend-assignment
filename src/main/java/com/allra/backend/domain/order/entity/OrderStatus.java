package com.allra.backend.domain.order.entity;

/**
 * 주문 상태를 나타내는 Enum 클래스
 * 
 * <p>주문 생성부터 결제 완료, 실패, 취소까지의 전체 라이프사이클을 표현합니다.</p>
 * 각 상태는 주문과 결제의 흐름에 따라 순차적으로 변경됩니다.
 */
public enum OrderStatus {

    /** CREATED (주문 생성됨, 결제 전 상태) — '주문 대기' */
    CREATED,      

    /** PENDING (결제 요청이 외부 결제 API로 전달되어 처리 중인 상태) — '결제 대기' */
    PENDING,      

    /** COMPLETED (결제가 성공적으로 완료되어 주문 확정된 상태) — '결제 완료' */
    SUCCESS,    

    /** FAILED (결제 실패: 잔액 부족, 오류, API 실패 등) — '결제 실패' */
    FAILED,       

    /** CANCELED (사용자 또는 시스템에 의해 주문이 취소된 상태) — '주문 취소' */
    CANCELED;
    
    /** 결제가 아직 처리 중(PENDING or CREATED)인지 여부 */
    public boolean isPending() {
        return this == CREATED || this == PENDING;
    }

    /** 결제가 완료된 상태인지 여부 */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /** 주문이 이미 종료(완료·취소·실패) 상태인지 여부 */
    public boolean isFinished() {
        return this == SUCCESS || this == FAILED || this == CANCELED;
    }

    /** 주문 취소 가능 여부 (결제 완료 이전 상태만 허용) */
    public boolean canBeCanceled() {
        return this == CREATED || this == PENDING;
    }
}
