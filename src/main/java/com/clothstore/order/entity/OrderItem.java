package com.clothstore.order.entity;

import com.clothstore.product.entity.ProductColor;
import com.clothstore.product.entity.ProductSize;
import com.clothstore.product.entity.ProductVariant;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSize productSize;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductColor productColor;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String sku;

    @NotNull
    @Column(nullable = false)
    @Positive
    private Integer quantity;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal unitPrice;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime updatedDate;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
