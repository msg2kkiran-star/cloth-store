package com.clothstore.product.controller;

import com.clothstore.product.dto.ProductVariantRequest;
import com.clothstore.product.dto.ProductVariantResponse;
import com.clothstore.product.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> getAll(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productVariantService.getVariantsByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ProductVariantResponse> create(@PathVariable Long productId, @Valid @RequestBody ProductVariantRequest productVariantRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productVariantService.addVariant(productId, productVariantRequest));
    }

    @PutMapping("{variantId}")
    public ResponseEntity<ProductVariantResponse> update(@PathVariable Long productId, @PathVariable Long variantId,
                                                         @Valid @RequestBody ProductVariantRequest productVariantRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(productVariantService.updateVariant(productId, variantId, productVariantRequest));

    }

    @DeleteMapping("{variantId}")
    public ResponseEntity<ProductVariantResponse> delete(@PathVariable Long productId, @PathVariable Long variantId) {
        return ResponseEntity.status(HttpStatus.OK).body(productVariantService.deleteVariant(productId, variantId));
    }
}
