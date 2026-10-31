package com.clothstore.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {

    @NotNull
    private Long productVariantId;

    @NotNull
    @Positive
    private Integer quantity;
}
