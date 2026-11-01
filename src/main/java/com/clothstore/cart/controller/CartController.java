package com.clothstore.cart.controller;

import com.clothstore.cart.dto.AddToCartRequest;
import com.clothstore.cart.dto.CartResponse;
import com.clothstore.cart.dto.UpdateCartItemRequest;
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

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long customerId, @Valid @RequestBody AddToCartRequest addToCartRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService
                        .addItemToCart(customerId, addToCartRequest.getProductVariantId(), addToCartRequest.getQuantity()));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long customerId, @PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartService.updateCartItemQuantity(customerId, itemId, updateCartItemRequest.getQuantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long customerId, @PathVariable Long itemId) {
        cartService.deleteCartItem(customerId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
