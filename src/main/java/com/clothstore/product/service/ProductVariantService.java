package com.clothstore.product.service;

import com.clothstore.exception.DuplicateResourceException;
import com.clothstore.exception.ResourceNotFoundException;
import com.clothstore.product.dto.ProductVariantRequest;
import com.clothstore.product.dto.ProductVariantResponse;
import com.clothstore.product.entity.Product;
import com.clothstore.product.entity.ProductVariant;
import com.clothstore.product.repository.ProductRepo;
import com.clothstore.product.repository.ProductVariantRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ProductVariantService {

    private final ProductVariantRepo productVariantRepo;
    private final ProductRepo productRepo;

    public ProductVariantService(ProductVariantRepo productVariantRepo, ProductRepo productRepo) {
        this.productVariantRepo = productVariantRepo;
        this.productRepo = productRepo;
    }

    public ProductVariantResponse addVariant(Long productId, ProductVariantRequest productVariantRequest) {

        Product existingProduct = productRepo.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId
                ));

        ProductVariant newProductVariant = new ProductVariant();
        newProductVariant.setProduct(existingProduct);
        newProductVariant.setProductColor(productVariantRequest.getProductColor());
        newProductVariant.setProductSize(productVariantRequest.getProductSize());
        newProductVariant.setPrice(productVariantRequest.getPrice());

        String sku = productVariantRequest.getSku().trim().toUpperCase(Locale.ROOT);

        if (productVariantRepo.existsBySku(sku)) {
            throw new DuplicateResourceException(
                    "Variant already exists with SKU: " + sku
            );
        }

        newProductVariant.setSku(sku);
        newProductVariant.setStockQuantity(productVariantRequest.getStockQuantity());
        newProductVariant.setActive(true);

        return convertToProductVariantResponse(productVariantRepo.save(newProductVariant));
    }

    public List<ProductVariantResponse> getVariantsByProductId(Long productId) {

        productRepo.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId
                ));

        return convertToProductVariantResponse(productVariantRepo.findByProductIdAndActiveTrue(productId));

    }

    public ProductVariantResponse updateVariant(Long productId, Long variantId, ProductVariantRequest productVariantRequest) {


        productRepo.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId
                ));

        ProductVariant existingVariant = productVariantRepo.findByIdAndProductIdAndActiveTrue(variantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variant not found with id: " + variantId + " and productId: " + productId
                ));

        existingVariant.setStockQuantity(productVariantRequest.getStockQuantity());
        existingVariant.setPrice(productVariantRequest.getPrice());

        String sku = productVariantRequest.getSku()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (productVariantRepo.existsBySkuAndIdNot(sku, variantId)) {
            throw new DuplicateResourceException(
                    "Variant already exists with SKU: " + sku
            );
        }
        existingVariant.setSku(sku);
        existingVariant.setProductSize(productVariantRequest.getProductSize());
        existingVariant.setProductColor(productVariantRequest.getProductColor());

        return convertToProductVariantResponse(productVariantRepo.save(existingVariant));
    }

    public ProductVariantResponse deleteVariant(Long productId, Long variantId) {

        productRepo.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId
                ));

        ProductVariant existingVariant = productVariantRepo.findByIdAndProductIdAndActiveTrue(variantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variant not found with id: " + variantId + " and productId: " + productId
                ));

        existingVariant.setActive(false);

        return convertToProductVariantResponse(productVariantRepo.save(existingVariant));
    }

    private ProductVariantResponse convertToProductVariantResponse(ProductVariant savedProductVariant) {

        ProductVariantResponse response = new ProductVariantResponse();

        response.setId(savedProductVariant.getId());
        response.setProductId(savedProductVariant.getProduct().getId());
        response.setPrice(savedProductVariant.getPrice());
        response.setSku(savedProductVariant.getSku());
        response.setStockQuantity(savedProductVariant.getStockQuantity());
        response.setProductColor(savedProductVariant.getProductColor());
        response.setProductSize(savedProductVariant.getProductSize());

        return response;
    }

    private List<ProductVariantResponse> convertToProductVariantResponse(List<ProductVariant> productVariants) {

        List<ProductVariantResponse> productVariantsResponses = new ArrayList<>();

        for (ProductVariant productVariant : productVariants) {
            productVariantsResponses.add(convertToProductVariantResponse(productVariant));
        }

        return productVariantsResponses;
    }
}
