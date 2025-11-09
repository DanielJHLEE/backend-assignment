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
            "<b>사용자별 장바구니 관리 API</b><br>" +
            "특정 사용자(<b>userId</b>)를 기준으로 장바구니를 조회, 추가, 수정, 삭제할 수 있습니다.<br>" +
            "장바구니는 <b>주문 전 임시 저장소</b>로, <b>재고와 무관하게</b> 상품을 담을 수 있습니다.<br>" +
            "더미 데이터: <b>16개</b>";

    public static final String CART_GET_ALL_DESC = """
        🧾 <b>사용자 장바구니 목록 조회</b><br>
        특정 사용자(<code>userId</code>)의 장바구니 전체 목록을 조회합니다.<br>
        각 장바구니에는 담긴 상품의 수량(<code>quantity</code>), 품절 여부(<code>soldOut</code>), 가격 정보가 포함됩니다.<br><br>
        ✅ <b>요청 예시</b>: <code>GET /api/users/1/carts</code><br>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;List&lt;UserCartResponseDto&gt;&gt;</code>
        """;

    public static final String CART_GET_DETAIL_DESC = """
        📋 <b>장바구니 상세 조회</b><br>
        특정 장바구니(<code>cartId</code>)의 상세 정보를 조회합니다.<br>
        해당 장바구니에 담긴 상품 리스트를 모두 반환합니다.<br><br>
        ✅ <b>요청 예시</b>: <code>GET /api/users/1/carts/2</code><br>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;List&lt;CartsIdDetailResponseDto&gt;&gt;</code>
        """;

    public static final String CART_GET_ITEM_DETAIL_DESC = """
        📦 <b>장바구니 아이템 상세 조회</b><br>
        장바구니 내 개별 상품(<code>cartItemId</code>)을 기준으로 상세 정보를 조회합니다.<br>
        상품명, 브랜드, 가격, 품절 여부, 수량 등의 정보를 제공합니다.<br><br>
        ✅ <b>요청 예시</b>: <code>GET /api/users/1/carts/2/items/5</code><br>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;CartItemsDetailResponseDto&gt;</code>
        """;

    public static final String CART_POST_ADD_ITEM_DESC = """
        ➕ <b>상품을 장바구니에 추가</b><br>
        사용자의 장바구니에 상품을 추가합니다.<br>
        - 장바구니가 없으면 자동으로 생성됩니다.<br>
        - 동일한 상품이 이미 존재할 경우 수량이 <code>+1</code> 증가합니다.<br>
        - 품절(<code>soldOut</code>) 또는 재고(<code>stock</code>) 여부와 관계없이 담을 수 있습니다.<br><br>

        ✅ <b>요청 예시</b>:<br>
        <pre>{
            "userId": 1,
            "productId": 1001
        }</pre>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;AddCartItemsResponseDto&gt;</code><br>
        성공 시 <code>200 OK</code> 반환.
        """;

    public static final String CART_PATCH_UPDATE_ITEM_DESC = """
        🔄 <b>장바구니 상품 수량 수정</b><br>
        장바구니에 담긴 상품의 수량을 변경합니다.<br>
        - 존재하지 않는 상품일 경우 <code>BusinessException</code> 발생<br>
        - 수량은 1 이상만 허용됩니다.<br><br>

        ✅ <b>요청 예시</b>:<br>
        <pre>{
            "quantity": 3
        }</pre>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;UpdateCartItemResponseDto&gt;</code><br>
        성공 시 <code>200 OK</code> 반환.
        """;

    public static final String CART_DELETE_ITEM_DESC = """
        ❌ <b>장바구니 상품 삭제</b><br>
        특정 장바구니(<code>cartId</code>) 내에서 개별 상품(<code>cartItemId</code>)을 삭제합니다.<br>
        삭제 후 남은 아이템이 없을 경우에도 장바구니는 유지됩니다.<br><br>
        ✅ <b>요청 예시</b>: <code>DELETE /api/users/1/carts/2/items/5</code><br>
        ✅ <b>응답 예시</b>:<br>
        <pre>{
            "status": "OK",
            "message": "상품이 장바구니에서 삭제되었습니다.",
            "data": null
        }</pre>
        성공 시 <code>200 OK</code> 반환.
        """;

    public static final String CART_DELETE_CART_DESC = """
        🗑️ <b>전체 장바구니 삭제</b><br>
        특정 사용자(<code>userId</code>)의 장바구니 전체를 삭제합니다.<br>
        장바구니에 담긴 모든 상품이 함께 삭제됩니다.<br><br>
        ✅ <b>요청 예시</b>: <code>DELETE /api/users/1/carts/2</code><br>
        ✅ <b>응답 예시</b>:<br>
        <pre>{
            "status": "OK",
            "message": "장바구니가 모두 비워졌습니다.",
            "data": null
        }</pre>
        성공 시 <code>200 OK</code> 반환.
        """;


    /* ==========================================================
    * 🧾 주문 & 결제 API
    * ========================================================== */
    public static final String ORDER_NAME = "🧾 주문 / 결제 API";
    public static final String ORDER_DESC = """
        <b>주문 생성 → 결제 요청 → 결제 결과 조회 → 주문 취소</b>까지의 전체 결제 프로세스를 관리합니다.<br><br>
        모든 결제 및 주문 동작은 <b>Mock 결제 API</b>를 통해 시뮬레이션되며,<br>
        각 단계별 상태 전환 (<code>CREATED → PENDING → SUCCESS / FAILED → CANCELED</code>) 을 테스트할 수 있습니다.<br><br>

        ⚙️ <b>사전 조건 (Pre-condition)</b><br>
        • 주문을 생성하기 전, 사용자는 반드시 장바구니에 상품을 추가해야 합니다.<br>
        • 장바구니에 담긴 상품 정보를 기반으로 주문이 생성됩니다.<br>
        • 장바구니 내 상품이 없을 경우 주문 생성이 불가합니다.<br><br>

        🔁 <b>전체 Flow</b><br>
        1️⃣ 장바구니 추가 (<code>CartService.addProductsToCart()</code>)<br>
        2️⃣ 주문 생성 (<code>OrderService.createOrder()</code>)<br>
        3️⃣ 결제 요청 (<code>PaymentService.processPayment()</code>)<br>
        4️⃣ 결제 결과 조회 (<code>PaymentService.checkPaymentResult()</code>)<br>
        5️⃣ 주문 취소 (<code>PaymentService.cancelOrder()</code>)
        """;

    public static final String ORDER_CREATE_DESC = """
        🧾 <b>주문 생성 (Create Order)</b><br>
        사용자의 장바구니를 기반으로 주문을 생성합니다.<br><br>

        ⚙️ <b>처리 절차</b><br>
        • 사용자 ID(<code>userId</code>) 기준으로 장바구니 조회<br>
        • 장바구니가 비어 있으면 <code>400 Bad Request</code> 반환<br>
        • 상품 재고 검증 및 총 결제 금액 계산<br>
        • Mock API에 주문 생성 요청 (<code>/api/mock/order</code>)<br>
        • Mock 응답의 주문번호(<code>orderId</code>)를 DB에 저장하고 상태는 <code>CREATED</code><br>
        • PaymentLog에도 동일 내역 기록<br><br>

        ⚠️ <b>사전 조건 (Pre-condition)</b><br>
        • 사용자는 최소 1개 이상의 상품을 장바구니에 담아야 합니다.<br>
        • <b>장바구니 내 상품 중 하나라도 재고가 부족하면 주문 생성이 거부됩니다.</b><br><br>

        ✅ <b>요청 예시</b>: <code>POST /api/orders/{userId}</code><br>
        ✅ <b>응답 예시</b>:<pre>{
        "orderId": "ORD_20251109_123456",
        "status": "CREATED",
        "message": "Order created successfully"
        }</pre>
        """;

    public static final String ORDER_PAYMENT_REQUEST_DESC = """
        💳 <b>결제 요청 (Request Payment)</b><br>
        생성된 주문 ID를 기반으로 결제를 요청합니다.<br><br>

        ⚙️ <b>처리 절차</b><br>
        • Mock 결제 API(<code>/api/mock/payment</code>) 호출<br>
        • 유효하지 않은 주문번호 또는 금액이 0 이하일 경우 즉시 <code>FAILED</code><br>
        • 정상 요청은 <code>PENDING</code> 상태로 저장되고,<br>
        내부 Mock 스레드에서 약 8~15초 후 <code>SUCCESS</code> 또는 <code>FAILED</code> 로 자동 전환<br>
        • 모든 요청/응답 내역은 <code>PaymentLogEntity</code>에 기록됩니다.<br><br>

        ⚠️ <b>사전 조건</b><br>
        • 주문 상태가 <code>CREATED</code> 여야 결제 요청이 가능합니다.<br><br>

        ✅ <b>요청 예시</b>: <code>POST /api/orders/{orderId}/payment</code><br>
        ✅ <b>응답 예시</b>:<pre>{
        "status": "PENDING",
        "transactionId": "txn_12345678",
        "message": "Payment request received. Processing..."
        }</pre>
        """;

    public static final String ORDER_PAYMENT_RESULT_DESC = """
        🔄 <b>결제 결과 조회 (Check Payment Result)</b><br>
        Mock 결제 API에서 현재 결제 상태를 조회하고 DB 상태를 동기화합니다.<br><br>

        ⚙️ <b>처리 절차</b><br>
        • Mock API(<code>/api/mock/payment/result/{orderId}</code>)를 호출<br>
        • 최신 결제 상태를 조회 후 <code>PaymentLog</code>와 <code>Order</code>의 상태를 업데이트<br>
        • 상태별 동작:<br>
        └ <code>SUCCESS</code> → 주문 확정<br>
        └ <code>FAILED</code> → 주문 실패<br>
        └ <code>PENDING</code> → 결제 진행 중<br><br>

        ⚠️ <b>사전 조건</b><br>
        • 결제 요청(PENDING)이 선행되어야 합니다.<br><br>

        ✅ <b>요청 예시</b>: <code>GET /api/orders/{orderId}/payment/result</code><br>
        ✅ <b>응답 예시</b>:<pre>{
        "status": "SUCCESS",
        "transactionId": "txn_abc123",
        "message": "Payment completed successfully"
        }</pre>
        """;

    public static final String ORDER_CANCEL_DESC = """
        ❌ <b>주문 취소 (Cancel Order)</b><br>
        특정 주문을 취소하고, 결제 로그에 <code>CANCELED</code> 상태를 기록합니다.<br><br>

        ⚙️ <b>처리 절차</b><br>
        • Mock API(<code>/api/mock/order/cancel</code>) 호출<br>
        • 현재 결제 상태에 따라 취소 가능 여부를 판별<br>
        └ <code>PENDING / SUCCESS</code> → 취소 가능<br>
        └ <code>FAILED</code> → 취소 불가<br>
        • 취소 성공 시 PaymentLog에 <code>CANCELED</code> 로그 추가<br>
        • OrderEntity 상태를 <code>CANCELED</code> 로 동기화<br><br>

        ⚠️ <b>사전 조건</b><br>
        • 주문은 결제 완료(SUCCESS) 또는 진행 중(PENDING) 상태여야 취소 가능<br><br>

        ✅ <b>요청 예시</b>: <code>POST /api/orders/{orderId}/cancel</code><br>
        ✅ <b>응답 예시</b>:<pre>{
        "status": "CANCELED",
        "message": "Order canceled successfully"
        }</pre>
        """;



    /* ==========================================================
    * 🧩 Mock API (외부 시뮬레이션)
    * ========================================================== */
    public static final String MOCK_NAME = "🧩 Mock 결제 / 주문 API";
    public static final String MOCK_DESC = """
        <b>외부 PG사 및 주문 서버를 시뮬레이션하는 Mock API</b><br>
        주문 생성 → 결제 요청 → 결제 상태 조회 → 주문 취소의 전체 흐름을 테스트할 수 있습니다.<br><br>

        ⚙️ <b>Mock 데이터 구조</b><br>
        - 모든 결제 상태는 <code>ConcurrentHashMap&lt;orderId, status&gt;</code> 에 임시 저장됩니다.<br>
        - 서버 재시작 시 초기화되며, DB에는 저장되지 않습니다.<br>
        - 등록되지 않은 <code>orderId</code> 조회/취소 시 <code>NOT_FOUND</code> 반환.<br><br>

        🔁 <b>테스트 순서</b><br>
        ① 주문 생성 → ② 결제 요청(PENDING) → ③ 결제 상태 조회(SUCCESS/FAILED) → ④ 주문 취소(CANCELED)<br><br>

        💡 <b>참고</b><br>
        각 단계는 독립 호출 가능하지만, 실제 테스트 시 위 순서를 따르는 것을 권장합니다.
        """;

    public static final String MOCK_ORDER_CREATE_DESC = """
        📦 <b>Mock 주문 생성</b><br>
        - 사용자 ID(<code>userId</code>), 상품 목록(<code>products</code>), 총 결제 금액(<code>amount</code>)을 기반으로 주문을 생성합니다.<br>
        - 요청 시 주문번호(<code>ORD_yyyyMMdd_HHmmss_random</code>)가 자동 생성되며, 상태는 항상 <code>CREATED</code>로 반환됩니다.<br><br>

        ⚙️ <b>요청 유효성 규칙</b><br>
        • <code>userId</code>가 null 또는 0 이하 → <code>INVALID_USER</code><br>
        • <code>products</code>가 비어있거나 null → <code>INVALID_PRODUCT_LIST</code><br><br>

        📤 <b>Request Example</b><br>
        <pre>{
        "userId": 1,
        "products": [
            {
            "id": 1,
            "name": "티셔츠",
            "brand": "나이키",
            "category": "의류",
            "price": 45000,
            "stock": 5,
            "soldOut": false
            },
            {
            "id": 2,
            "name": "운동화",
            "brand": "아디다스",
            "category": "신발",
            "price": 90000,
            "stock": 3,
            "soldOut": false
            }
        ],
        "amount": 135000
        }</pre><br>

        📥 <b>Response Example</b><br>
        ✅ <i>정상 요청</i><br>
        <pre>{
        "orderId": "ORD_20251110_003247_171169",
        "status": "CREATED",
        "message": "Order created successfully for userId=1 with 2 product(s). Total amount: 135000"
        }</pre><br>

        ❌ <i>유효하지 않은 사용자 ID</i><br>
        <pre>{
        "orderId": null,
        "status": "INVALID_USER",
        "message": "Invalid userId. Order cannot be created."
        }</pre><br>

        ❌ <i>상품 목록 없음</i><br>
        <pre>{
        "orderId": null,
        "status": "INVALID_PRODUCT_LIST",
        "message": "No products provided. At least one product is required."
        }</pre>
        """;

    public static final String MOCK_PAYMENT_DESC = """
        💳 <b>Mock 결제 요청</b><br>
        - 주문번호(<code>orderId</code>)와 결제 금액(<code>amount</code>)을 전달하여 결제 프로세스를 시뮬레이션합니다.<br>
        - 요청 시 유효성 검증이 수행됩니다.<br>
        &nbsp;&nbsp;• <code>orderId</code>가 비어 있으면 → <code>INVALID_ORDER_ID</code><br>
        &nbsp;&nbsp;• <code>amount ≤ 0</code>이면 → 즉시 <code>FAILED</code><br><br>
        - 정상 요청의 경우:<br>
        &nbsp;&nbsp;① 즉시 <code>PENDING</code> 상태로 응답<br>
        &nbsp;&nbsp;② 약 8~15초 후 내부적으로 <code>SUCCESS</code> (80%) / <code>FAILED</code> (20%) 로 전환됩니다.<br><br>

        📤 <b>Request Example</b><br>
        <pre>{
        "orderId": "ORD_20251110_003247_171169",
        "amount": 135000
        }</pre><br>

        📥 <b>Response Example</b><br>
        ✅ <i>정상 요청 (PENDING)</i><br>
        <pre>{
        "status": "PENDING",
        "transactionId": "txn_5f3a6b82",
        "message": "Payment request for orderId=ORD_20251110_003247_171169 received. Processing..."
        }</pre>
        
        ❌ <i>유효하지 않은 주문번호</i><br>
        <pre>{
        "status": "INVALID_ORDER_ID",
        "transactionId": "txn_7b9d83ac",
        "message": "Invalid orderId. Payment cannot be processed."
        }</pre>

        ❌ <i>잘못된 결제 금액 (0 이하)</i><br>
        <pre>{
        "status": "FAILED",
        "transactionId": "txn_2e7f9a01",
        "message": "Payment failed immediately: invalid amount (0)"
        }</pre>
        """;

    public static final String MOCK_PAYMENT_RESULT_DESC = """
        🔍 <b>Mock 결제 상태 조회</b><br>
        - 특정 주문번호(<code>orderId</code>)의 현재 결제 상태를 반환합니다.<br>
        - 상태는 결제 요청 시점 이후 내부적으로 비동기 전환됩니다.<br><br>
        - ⚙️ 결제 요청 시 등록된 <code>paymentStatusMap</code> 기준으로 조회되며, 존재하지 않으면 <code>NOT_FOUND</code> 반환.<br><br>

        📘 <b>가능한 상태값</b><br>
        • <code>PENDING</code> → 결제 진행 중<br>
        • <code>SUCCESS</code> → 결제 완료<br>
        • <code>FAILED</code> → 결제 실패<br>
        • <code>NOT_FOUND</code> → 존재하지 않는 주문 ID<br><br>

        📥 <b>Response Example</b><br>
        ✅ <i>결제 성공</i><br>
        <pre>{
        "status": "SUCCESS",
        "transactionId": "txn_5f3a6b82",
        "message": "Payment status for orderId=ORD_20251110_003247_171169 is SUCCESS"
        }</pre>

        ❌ <i>주문 ID 미존재</i><br>
        <pre>{
        "status": "NOT_FOUND",
        "transactionId": "txn_ORD_20251110_003247_171169",
        "message": "Payment status for orderId=ORD_20251110_003247_171169 is NOT_FOUND"
        }</pre>
        """;

    public static final String MOCK_ORDER_CANCEL_DESC = """
    ❌ <b>Mock 주문 취소</b><br>
    - 지정된 주문번호(<code>orderId</code>)를 기준으로 주문을 취소합니다.<br>
    - 현재 결제 상태에 따라 결과가 달라집니다.<br><br>

    🔎 <b>상태별 처리 규칙</b><br>
    • <code>PENDING</code> → 취소 가능 → <code>CANCELED</code><br>
    • <code>SUCCESS</code> → 취소(환불 개념) 가능 → <code>CANCELED</code><br>
    • <code>FAILED</code> → 이미 결제가 실패했으므로 취소 불가 → <code>CANNOT_CANCEL</code><br>
    • <code>NOT_FOUND</code> → 주문 ID 미존재 → <code>NOT_FOUND</code><br><br>

    📤 <b>Request Example</b><br>
    <pre>{
      "orderId": "ORD_20251110_003247_171169"
    }</pre><br>

    📥 <b>Response Example</b><br>
    ✅ <i>성공 (PENDING / SUCCESS 상태 취소 시)</i><br>
    <pre>{
      "status": "CANCELED",
      "message": "Order canceled successfully for orderId=ORD_20251110_003247_171169"
    }</pre>
    
    ❌ <i>취소 불가 (FAILED 상태)</i><br>
    <pre>{
      "status": "CANNOT_CANCEL",
      "message": "OrderId=ORD_20251110_003247_171169 cannot be canceled because payment has already failed."
    }</pre>
    
    ❌ <i>존재하지 않는 주문</i><br>
    <pre>{
      "status": "NOT_FOUND",
      "message": "OrderId=ORD_20251110_003247_171169 not found. Cancel request ignored."
    }</pre>
    """;

    /* ==========================================================
    * 💳 결제 로그 / 히스토리 API
    * ========================================================== */
    public static final String PAYMENT_NAME = "💳 결제 로그 / 히스토리 API";
    public static final String PAYMENT_DESC =
            "<b>주문별 결제 이력(PaymentLogEntity)</b>을 조회하기 위한 API입니다.<br>" +
            "각 주문(<code>orderId</code>)을 기준으로 결제 요청, 성공, 실패, 취소 등 전체 로그를 시간 순서대로 반환합니다.<br><br>" +
            "이 API는 결제 진행 로직(OrderController → PaymentService)과는 별도로,<br>" +
            "<b>운영자 또는 개발자 테스트용</b>으로 결제 상태 추적에 활용됩니다.<br><br>" +
            "✅ <b>예시 흐름</b>:<br>" +
            "1️⃣ 주문 생성 → 2️⃣ 결제 요청 → 3️⃣ 결제 결과 확인 → 4️⃣ 취소 → 5️⃣ <b>결제 이력 조회</b>";

    public static final String PAYMENT_GET_HISTORY_DESC = """
        🧾 <b>주문별 결제 로그 조회</b><br>
        특정 주문(<code>orderId</code>)에 대한 전체 결제 이력을 반환합니다.<br>
        각 로그에는 <b>status</b>, <b>transactionId</b>, <b>message</b>, <b>createdAt</b> 정보가 포함됩니다.<br><br>
        ✅ <b>요청 예시</b>: <code>GET /api/payments/{orderId}/logs</code><br>
        ✅ <b>응답 예시</b>: <pre>[
            {
                "order": { "id": 1001 },
                "transactionId": "ORD_20251110_003247_171169",
                "status": "CREATED",
                "message": "Order created successfully",
                "createdAt": "2025-11-10T14:00:00"
            },
            {
                "order": { "id": 1001 },
                "transactionId": "txn_5f3a6b82",
                "status": "SUCCESS",
                "message": "Payment completed successfully",
                "createdAt": "2025-11-10T14:05:00"
            }
        ]</pre><br>
        반환 형식: <code>ApiResponseDto&lt;List&lt;PaymentLogEntity&gt;&gt;</code>
        """;
}
