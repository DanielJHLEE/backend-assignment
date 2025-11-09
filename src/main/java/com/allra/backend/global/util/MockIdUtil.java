package com.allra.backend.global.util;

/**
 * Mock ID 변환 유틸리티
 * 
 * <p>DB PK(Long) ↔ Mock API용 주문번호(String) 간 변환을 담당합니다.</p>
 */
public class MockIdUtil {

    private static final String PREFIX = "ORD_";

    /**
     * DB PK(Long) → Mock 주문번호(String)
     * ex) 123 → "ORD_123"
     */
    public static String toMockOrderId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        return PREFIX + id;
    }

    /**
     * Mock 주문번호(String) → DB PK(Long)
     * ex) "ORD_123" → 123
     */
    public static Long toEntityId(String mockOrderId) {
        if (mockOrderId == null || !mockOrderId.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Invalid mock orderId: " + mockOrderId);
        }
        return Long.parseLong(mockOrderId.substring(PREFIX.length()));
    }

    // 유틸 클래스이므로 인스턴스화 방지
    private MockIdUtil() {}
}
