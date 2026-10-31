package com.clothstore.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponse {
    private Long id;
    private Long customerId;
    private List<CartItemResponse> items;
    private BigDecimal grandTotal;
}
