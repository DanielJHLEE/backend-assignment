package com.allra.backend.docs.swagger;

/**
 * 📘 Swagger Tags
 * 각 도메인(Controller)별 Swagger 그룹 및 API 설명 상수를 정의하는 클래스
 *
 * <p>컨트롤러의 @Tag(name, description) 또는
 * 메서드의 @Operation(summary, description)에 참조하여 사용합니다.</p>
 */
public final class SwaggerTags {

    private SwaggerTags() {} // 인스턴스화 방지

    /* ==========================================================
     * 👤 사용자 API
     * ========================================================== */
    public static final String USER_NAME = "👤 사용자 API";
    public static final String USER_DESC =
            "<b>회원 정보 조회 관련 API</b><br>" +
            "전체 사용자 목록과 ID 기반 상세 조회 기능을 제공합니다.<br>" +
            "더미 데이터: <b>5개</b>";

    public static final String USER_GET_ALL_DESC = """
        👥 <b>전체 사용자 목록 조회</b><br>
        DB에 등록된 모든 사용자 정보를 조회합니다.<br>
        반환 형식: <code>ApiResponseDto&lt;List&lt;UserResponseDto&gt;&gt;</code>
        """;

    public static final String USER_GET_DETAIL_DESC = """
        🔍 <b>사용자 상세 조회</b><br>
        사용자 ID를 기준으로 단일 사용자 정보를 조회합니다.<br>
        존재하지 않을 경우 <code>404 Not Found</code> 응답을 반환합니다.<br>
        반환 형식: <code>ApiResponseDto&lt;UserResponseDto&gt;</code>
        """;


    /* ==========================================================
     * 📦 상품 API
     * ========================================================== */
    public static final String PRODUCT_NAME = "📦 상품 API";
    public static final String PRODUCT_DESC =
            "<b>상품 목록 및 상세 조회 관련 API</b><br>" +
            "상품 목록 및 상세 조회 관련 엔드포인트<br>" +

            "<b>카테고리</b>, <b>상품명</b>, <b>가격 범위</b> 기반 검색과 페이징 기능을 제공합니다.";

    public static final String PRODUCT_GET_ALL_DESC = """
        🗂️ <b>상품 목록 조회</b><br>
        전체 상품을 조회하며, 다음 조건으로 검색 가능합니다:<br>
        - <b>category</b>, <b>name</b>, <b>minPrice</b>, <b>maxPrice</b><br>
        - <b>페이지(page)</b>, <b>사이즈(size)</b> 기반 페이징 처리<br><br>
        반환 형식: <code>ApiResponseDto&lt;PageResponseDto&lt;ProductResponseDto&gt;&gt;</code>
        """;

    public static final String PRODUCT_GET_DETAIL_DESC = """
        🔎 <b>상품 상세 조회</b><br>
        상품 ID를 기준으로 단일 상품 정보를 조회합니다.<br>
        존재하지 않을 경우 <code>404 Not Found</code> 응답을 반환합니다.<br>
        반환 형식: <code>ApiResponseDto&lt;ProductResponseDto&gt;</code>
        """;


    /* ==========================================================
     * 🛒 장바구니 API
     * ========================================================== */
    public static final String CART_NAME = "🛒 장바구니 API";
    public static final String CART_DESC =
            "<b>사용자별 장바구니 조회 관련 API</b><br>" +
            "특정 사용자<b>(userId)</b>를 기준으로 장바구니 목록 및 상품 상세 정보를 제공합니다.<br>" +
            "더미 데이터: <b>16개</b>";

    public static final String CART_GET_ALL_DESC = """
        🧾 <b>사용자 장바구니 목록 조회</b><br>
        특정 사용자(userId)의 장바구니 정보를 조회합니다.<br>
        각 상품의 품절 상태(<code>soldOut</code>), 수량(<code>quantity</code>) 정보를 함께 제공합니다.<br>
        반환 형식: <code>ApiResponseDto&lt;List&lt;CartResponseDto&gt;&gt;</code>
        """;

    public static final String CART_GET_DETAIL_DESC = """
        📋 <b>장바구니 상세 조회</b><br>
        특정 장바구니(cartId)의 상세 정보를 조회합니다.<br>
        장바구니 <b>ID</b>에 해당하는 상품 리스트를 반환합니다.<br>
        반환 형식: <code>ApiResponseDto&lt;List&lt;CartsIdDetailResponseDto&gt;&gt;</code>
        """;

    public static final String CART_GET_ITEM_DETAIL_DESC = """
        📦 <b>장바구니 아이템 상세 조회</b><br>
        장바구니 내 개별 상품(cartItemId)을 기준으로 정보를 조회합니다.<br>
        <b>상품명</b>, <b>브랜드</b>, <b>가격</b>, <b>품절 여부</b>, <b>수량</b> 등의 상세 정보를 제공합니다.<br>
        반환 형식: <code>ApiResponseDto&lt;CartItemsDetailResponseDto&gt;</code>
        """;

    public static final String CART_POST_ADD_ITEM_DESC = """
        ➕ <b>상품을 장바구니에 추가</b><br>
        사용자의 장바구니에 상품을 추가하거나, 이미 존재하는 상품의 수량을 증가시킵니다.<br><br>
        ✅ <b>요청 예시</b>:<br>
        <pre>{
            "userId" : 23
            "productId": 1001
        }</pre>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;AddCartItemsResponseDto&gt;</code><br><br>
        성공 시 <code>201 Created</code> 반환.
        """;

    public static final String CART_PATCH_UPDATE_ITEM_DESC = """
        🔄 <b>장바구니 상품 수량 수정</b><br>
        기존 장바구니에 담긴 상품의 수량을 변경합니다.<br><br>
        ✅ <b>요청 예시</b>:<br>
        <pre>{
            "quantity": 3
        }</pre>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;UpdateCartItemResponseDto&gt;</code><br><br>
        성공 시 <code>200 OK</code> 반환.
        """;

    public static final String CART_DELETE_ITEM_DESC = """
        ❌ <b>장바구니 아이템 삭제</b><br>
        특정 장바구니(<b>cartId</b>) 내 개별 상품(<b>cartItemId</b>)을 삭제합니다.<br><br>
        ✅ <b>요청 예시</b>:<br>
        <code>DELETE /api/users/1/carts/2/items/5</code><br><br>
        ✅ <b>응답 예시</b>:<br>
        <pre>{
            "status": "OK",
            "message": "장바구니 상품이 삭제되었습니다.",
            "data": null
        }</pre>
        성공 시 <code>200 OK</code> 반환.
        """;

    public static final String CART_DELETE_CART_DESC = """
        🗑️ <b>전체 장바구니 삭제</b><br>
        특정 사용자(<b>userId</b>)의 장바구니 전체를 삭제합니다.<br><br>
        ✅ <b>요청 예시</b>:<br>
        <code>DELETE /api/users/1/carts/2</code><br><br>
        ✅ <b>응답 예시</b>:<br>
        <pre>{
            "status": "OK",
            "message": "장바구니가 전체 삭제되었습니다.",
            "data": null
        }</pre>
        성공 시 <code>200 OK</code> 반환.
        """;

    /* ==========================================================
     * 🧾 주문 & 결제 API
     * ========================================================== */
    public static final String ORDER_NAME = "🧾 주문 / 결제 API";
    public static final String ORDER_DESC =
            "<b>주문 생성 → 결제 요청 → 결제 결과 조회 → 주문 취소</b>까지의 전체 결제 프로세스를 관리합니다.<br>" +
            "모든 결제 관련 동작은 <b>Mock 결제 API</b>를 통해 시뮬레이션되며,<br>" +
            "결제 상태 전환 (PENDING → SUCCESS / FAILED)</b>을 모사합니다.<br><br>" +
            "전체 Flow:<br>" +
            "1️⃣ <b>주문 생성</b> → 2️⃣ <b>결제 요청</b> → 3️⃣ <b>결제 결과 조회</b> → 4️⃣ <b>주문 취소</b>";

    public static final String ORDER_CREATE_DESC = """
        🧾 <b>주문 생성</b><br>
        사용자의 장바구니를 기반으로 주문을 생성합니다.<br>
        - 장바구니 상품 수량/재고 검증<br>
        - 주문 총액 계산 및 DB 저장<br>
        - Mock API 호출로 주문번호 생성 (CREATED 상태)<br><br>
        ✅ <b>요청 예시</b>: <code>POST /api/orders/{userId}</code><br>
        ✅ <b>응답 예시</b>: <pre>{
            "orderId": "ORD_20251109_123456",
            "status": "CREATED",
            "message": "Order created successfully"
        }</pre>
        """;

    public static final String ORDER_PAYMENT_REQUEST_DESC = """
        💳 <b>결제 요청</b><br>
        생성된 주문 ID를 기반으로 결제를 시도합니다.<br>
        - Mock 결제 API 호출<br>
        - 최초 응답은 항상 <code>PENDING</code><br>
        - Mock 내부에서 8초 후 자동으로 SUCCESS / FAILED 상태로 전환<br><br>
        ✅ <b>요청 예시</b>: <code>POST /api/orders/{orderId}/payment</code><br>
        ✅ <b>응답 예시</b>: <pre>{
            "status": "PENDING",
            "transactionId": "txn_12345678",
            "message": "Payment request received. Processing..."
        }</pre>
        """;

    public static final String ORDER_PAYMENT_RESULT_DESC = """
        🔄 <b>결제 결과 조회</b><br>
        Mock 결제 API로부터 현재 결제 상태를 조회합니다.<br>
        - 내부적으로 Mock 서버의 <code>paymentStatusMap</code> 상태를 확인<br>
        - PENDING → SUCCESS / FAILED 로 전환된 결과를 DB(Order + PaymentLog)에 동기화<br><br>
        ✅ <b>요청 예시</b>: <code>GET /api/orders/{orderId}/payment/result</code><br>
        ✅ <b>응답 예시</b>: <pre>{
            "status": "SUCCESS",
            "transactionId": "txn_abc123",
            "message": "Payment completed successfully"
        }</pre>
        """;

    public static final String ORDER_CANCEL_DESC = """
        ❌ <b>주문 취소</b><br>
        특정 주문을 취소하고, 결제 로그에 <b>CANCELED</b> 상태로 기록합니다.<br>
        - Mock API 호출로 즉시 CANCELED 상태 반환<br>
        - 기존 결제 금액 그대로 유지<br>
        - OrderEntity 상태도 CANCELED 로 동기화<br><br>
        ✅ <b>요청 예시</b>: <code>POST /api/orders/{orderId}/cancel</code><br>
        ✅ <b>응답 예시</b>: <pre>{
            "status": "CANCELED",
            "message": "Order canceled successfully"
        }</pre>
        """;


    /* ==========================================================
     * 🧩 Mock API (외부 시뮬레이션)
     * ========================================================== */
    public static final String MOCK_NAME = "🧩 Mock 결제 / 주문 API";
    public static final String MOCK_DESC =
            "<b>외부 PG사 또는 주문 서버를 시뮬레이션하는 Mock API</b><br>" +
            "테스트 환경에서 결제, 주문 생성, 취소 요청을 흉내내며, <br>" +
            "실제 결제 연동 구조와 동일한 응답 구조를 제공합니다.";

    public static final String MOCK_ORDER_CREATE_DESC = """
        📦 <b>Mock 주문 생성</b><br>
        - 요청 시 주문번호 자동 생성 (ORD_yyyyMMdd_HHmmss_random)<br>
        - 상태값: CREATED
        """;

    public static final String MOCK_PAYMENT_DESC = """
        💳 <b>Mock 결제 요청</b><br>
        - 최초 상태: PENDING<br>
        - 8초 후 80% 확률로 SUCCESS / 20% 확률로 FAILED 로 전환<br>
        - 내부적으로 ConcurrentHashMap 에 상태 저장
        """;

    public static final String MOCK_PAYMENT_RESULT_DESC = """
        🔍 <b>Mock 결제 상태 조회</b><br>
        - 현재 결제 상태 (PENDING / SUCCESS / FAILED) 를 반환<br>
        - 실제 PG사의 비동기 결제완료 조회를 시뮬레이션함
        """;

    public static final String MOCK_ORDER_CANCEL_DESC = """
        ❌ <b>Mock 주문 취소</b><br>
        - 단순히 CANCELED 상태를 반환<br>
        - 실제 취소 API의 구조를 모사하여 테스트용으로 제공
        """;
}
