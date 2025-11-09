package com.allra.backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.allra.backend.global.exception.BusinessException;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID

    @Column(nullable = false, length = 100)
    private String name; // 상품명

    @Column(length = 50)
    private String brand; // 브랜드명

    @Column(length = 50)
    private String category; // 상품 카테고리

    @Column
    private Integer price; // 가격

    @Column(columnDefinition = "int DEFAULT 0")
    private Integer stock; // 재고 수량

    @Column(name = "sold_out", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean soldOut; // 품절 여부

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt; // 등록일

    public void validateStock(int requestedQty) {
        if (this.stock < requestedQty) {
            throw new BusinessException("재고가 부족한 상품이 포함되어 있어 주문을 생성할 수 없습니다. (" + this.name + ")");
        }
    }
}
