package com.clothstore.cart.controller;

import com.clothstore.cart.dto.AddToCartRequest;
import com.clothstore.cart.dto.CartResponse;
import com.clothstore.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/{customerId}/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long customerId, @Valid @RequestBody AddToCartRequest addToCartRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService
                        .addItemToCart(customerId, addToCartRequest.getProductVariantId(), addToCartRequest.getQuantity()));
    }
}
