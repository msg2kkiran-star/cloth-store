package com.clothstore.cart.dto;

import com.clothstore.product.entity.ProductColor;
import com.clothstore.product.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemResponse {
    private Long itemId;
    private Long productVariantId;
    private String productName;
    private ProductSize productSize;
    private ProductColor productColor;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
