package com.allra.backend.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allra.backend.domain.cart.dto.CartDto;
import com.allra.backend.domain.cart.entity.CartEntity;
import com.allra.backend.domain.cart.entity.CartItemEntity;
import com.allra.backend.domain.cart.repository.CartRepository;
import com.allra.backend.domain.product.entity.ProductEntity;
import com.allra.backend.domain.product.repository.ProductRepository;
import com.allra.backend.domain.user.entity.UserEntity;
import com.allra.backend.domain.user.repository.UserRepository;
import com.allra.backend.global.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Mock
    private UserRepository userRepository;
        
    @Mock
    private ProductRepository productRepository;
        
    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @Test
    @DisplayName("사용자 장바구니 조회 (userId 기준)")
    void testGetUserCartsByUserId() {
        ProductEntity product = ProductEntity.builder()
                .id(100L)
                .name("테스트상품")
                .price(10000)
                .brand("BrandA")
                .category("전자제품")
                .soldOut(false)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        CartEntity cart = CartEntity.builder()
                .id(1L)
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(cartRepository.findUserCartsByUserId(anyLong()))
                .thenReturn(List.of(cart));

        List<CartDto.UserCartResponseDto> result = cartService.getUserCartsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("테스트상품");
        assertThat(result.get(0).getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("사용자 특정 장바구니 상세 조회 성공 (cartId 기준)")
    void testGetCartsDetailByCartId() {
        ProductEntity product = ProductEntity.builder()
                .id(200L)
                .name("마우스")
                .price(25000)
                .brand("Logi")
                .category("주변기기")
                .soldOut(false)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(10L)
                .product(product)
                .quantity(3)
                .build();

        CartEntity cart = CartEntity.builder()
                .id(2L)
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(cartRepository.findCartsByUserIdAndCartId(anyLong(), anyLong()))
                .thenReturn(List.of(cart));

        List<CartDto.CartsIdDetailResponseDto> result = cartService.getCartsDetailByCartId(1L, 2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getProductName()).isEqualTo("마우스");
        assertThat(result.get(0).getItems().get(0).getPrice()).isEqualTo(25000);
        assertThat(result.get(0).getItems().get(0).getQuantity()).isEqualTo(3);
    }


    @Test
    @DisplayName("사용자 장바구니 아이템 단건 조회 성공")
    void testGetCartItemDetail_Success() {
        ProductEntity product = ProductEntity.builder()
                .id(10L)
                .name("노트북")
                .price(1200000)
                .build();

        CartItemEntity item = CartItemEntity.builder()
                .id(5L)
                .product(product)
                .quantity(1)
                .build();

        Mockito.when(cartRepository.findCartItemByIds(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.of(item));

        var result = cartService.getCartItemDetail(1L, 2L, 5L);
        assertThat(result.getProductName()).isEqualTo("노트북");
        assertThat(result.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 장바구니 아이템 단건 조회 실패 (존재하지 않음)")
    void testGetCartItemDetail_NotFound() {
        Mockito.when(cartRepository.findCartItemByIds(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        var result = cartService.getCartItemDetail(1L, 2L, 5L);
        assertThat(result).isNull();
    }

    @Test
	@DisplayName("같은 상품 id면 수량이 +1 된다")
	void testAddProductsToCart_SameProductIncreasesQuantity() {
		Long userId = 1L;
		Long productId = 10L;

		var user = new UserEntity(userId, "이재홍", "test@test.com", null);
		var product = ProductEntity.builder().id(productId).name("노트북").price(1000000).build();

		var existingItem = CartItemEntity.builder()
				.product(product)
				.quantity(1)
				.build();

		var cart = CartEntity.builder()
				.user(user)
				.items(List.of(existingItem))
				.build();

		Mockito.when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
		Mockito.when(productRepository.getByIdOrThrow(productId)).thenReturn(product);
		Mockito.when(cartRepository.findUserCartsByUserId(userId)).thenReturn(List.of(cart));

		var request = new CartDto.AddCartItemsRequestDto(userId, productId);

		cartService.addProductsToCart(request);

		assertThat(existingItem.getQuantity()).isEqualTo(2);
	}

	@Test
	@DisplayName("다른 상품 id면 새 CartItemEntity가 생성된다")
	void testAddProductsToCart_DifferentProductCreatesNewItem() {
		Long userId = 1L;

		var user = new UserEntity(userId, "이재홍", "test@test.com", null);

		var existingProduct = ProductEntity.builder().id(10L).name("노트북").price(1000000).build();
		var newProduct = ProductEntity.builder().id(20L).name("키보드").price(30000).build();

		var existingItem = CartItemEntity.builder()
				.product(existingProduct)
				.quantity(1)
				.build();

		var cart = CartEntity.builder()
				.user(user)
				.items(new java.util.ArrayList<>(List.of(existingItem)))
				.build();

		Mockito.when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
		Mockito.when(productRepository.getByIdOrThrow(20L)).thenReturn(newProduct);
		Mockito.when(cartRepository.findUserCartsByUserId(userId)).thenReturn(List.of(cart));

		var request = new CartDto.AddCartItemsRequestDto(userId, 20L);

		cartService.addProductsToCart(request);

		assertThat(cart.getItems()).hasSize(2); // 새 아이템 생성
		Mockito.verify(entityManager).persist(Mockito.any(CartItemEntity.class));
	}

    @Test
	@DisplayName("장바구니 상품 수량 수정 성공")
	void testUpdateCartItemQuantity_Success() {
		Long userId = 1L, cartId = 2L, cartItemId = 3L;

		var product = ProductEntity.builder().id(100L).name("모니터").price(200000).build();
		var cartItem = CartItemEntity.builder().id(cartItemId).product(product).quantity(1).build();

		Mockito.when(cartRepository.findCartItemByIds(userId, cartId, cartItemId))
				.thenReturn(Optional.of(cartItem));

		CartDto.UpdateCartItemRequestDto request = new CartDto.UpdateCartItemRequestDto(5);

		var result = cartService.updateCartItemQuantity(userId, cartId, cartItemId, request);

		assertThat(result.getQuantity()).isEqualTo(5);
		Mockito.verify(entityManager).flush();
	}

	@Test
	@DisplayName("장바구니 상품 수량 수정 실패 - 존재하지 않음")
	void testUpdateCartItemQuantity_NotFound() {
		Long userId = 1L, cartId = 2L, cartItemId = 99L;
		CartDto.UpdateCartItemRequestDto request = new CartDto.UpdateCartItemRequestDto(3);

		Mockito.when(cartRepository.findCartItemByIds(userId, cartId, cartItemId))
				.thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(
			com.allra.backend.global.exception.BusinessException.class,
			() -> cartService.updateCartItemQuantity(userId, cartId, cartItemId, request)
		);
	}

	@Test
	@DisplayName("장바구니 개별 상품 삭제 성공")
	void testDeleteCartItem_Success() {
		Long userId = 1L, cartId = 2L, cartItemId = 3L;

		var user = new UserEntity(userId, "이재홍", "jh@test.com", null);
		var product = ProductEntity.builder().id(10L).name("노트북").price(1000000).build();

		var cartItem = CartItemEntity.builder()
				.id(cartItemId)
				.cart(CartEntity.builder().id(cartId).user(user).build())
				.product(product)
				.quantity(1)
				.build();

		Mockito.when(cartRepository.findCartItemByIds(userId, cartId, cartItemId))
				.thenReturn(Optional.of(cartItem));

		cartService.deleteCartItem(userId, cartId, cartItemId);

		Mockito.verify(entityManager).remove(cartItem);
	}

	@Test
	@DisplayName("장바구니 개별 상품 삭제 실패 - 존재하지 않음")
	void testDeleteCartItem_NotFound() {
		Long userId = 1L, cartId = 2L, cartItemId = 99L;

		Mockito.when(cartRepository.findCartItemByIds(userId, cartId, cartItemId))
				.thenReturn(Optional.empty());

		assertThrows(BusinessException.class,
				() -> cartService.deleteCartItem(userId, cartId, cartItemId));
	}

	@Test
	@DisplayName("장바구니 전체 삭제 성공")
	void testDeleteEntireCart_Success() {
		Long userId = 1L, cartId = 10L;
		var user = new UserEntity(userId, "이재홍", "jh@test.com", null);
		var cart = CartEntity.builder().id(cartId).user(user).build();

		Mockito.when(cartRepository.findCartsByUserIdAndCartId(userId, cartId))
				.thenReturn(List.of(cart));

		cartService.deleteEntireCart(userId, cartId);

		Mockito.verify(entityManager).remove(cart);
	}

	@Test
	@DisplayName("장바구니 전체 삭제 실패 - 존재하지 않음")
	void testDeleteEntireCart_NotFound() {
		Long userId = 1L, cartId = 10L;

		Mockito.when(cartRepository.findCartsByUserIdAndCartId(userId, cartId))
				.thenReturn(List.of());

		assertThrows(BusinessException.class,
				() -> cartService.deleteEntireCart(userId, cartId));
	}
}   
