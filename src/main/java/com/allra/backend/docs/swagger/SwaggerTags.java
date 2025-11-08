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
            "<b>회원 정보 조회 관련 API엔드포인트</b><br>" +
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
            "<b>상품 목록 및 상세 조회 관련 API엔드포인트</b><br>" +

            "<b>상품 목록 및 상세 조회 관련 엔드포인트</b><br>" +

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
            "<b>사용자별 장바구니 조회 관련 엔드포인트</b><br>" +
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
            "productId": 1001,
            "quantity": 2
        }</pre>
        ✅ <b>응답 형식</b>: <code>ApiResponseDto&lt;AddCartItemsResponseDto&gt;</code><br><br>
        성공 시 <code>201 Created</code> 반환.
        """;
}
