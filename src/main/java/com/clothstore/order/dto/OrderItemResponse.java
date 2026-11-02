package com.clothstore.order.dto;

import com.clothstore.product.entity.ProductColor;
import com.clothstore.product.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {

    private Long id;
    private Long productVariantId;

    private String productName;
    private ProductSize productSize;
    private ProductColor productColor;
    private String sku;

    private Integer quantity;

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}