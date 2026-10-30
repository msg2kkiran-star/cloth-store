package com.clothstore.product.dto;

import com.clothstore.product.entity.ProductColor;
import com.clothstore.product.entity.ProductSize;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVariantRequest {

    @NotNull
    private ProductSize productSize;

    @NotNull
    private ProductColor productColor;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    @NotBlank
    @Size(max = 50)
    private String sku;
}
