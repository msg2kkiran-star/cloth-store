package com.clothstore.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductSize productSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductColor productColor;

    @Column(nullable = false)
    @Positive
    private BigDecimal price;

    @Column(nullable = false)
    @PositiveOrZero
    private Integer stockQuantity;

    @Column(name = "stock_code",
            unique = true,
            nullable = false,
            length = 50
    )
    private String sku;

    @PrePersist
    @PreUpdate
    public void normalizeSku() {
        if (this.sku != null) {
            this.sku = this.sku.trim().toUpperCase(Locale.ROOT);
        }
    }
}
