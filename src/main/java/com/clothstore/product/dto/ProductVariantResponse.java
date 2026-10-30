package com.clothstore.product.dto;

import com.clothstore.product.entity.ProductColor;
import com.clothstore.product.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private ProductSize productSize;
    private ProductColor productColor;
    private BigDecimal price;
    private Integer stockQuantity;
    private String sku;
}
