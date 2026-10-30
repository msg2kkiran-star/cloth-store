package com.clothstore.product.service;

import com.clothstore.exception.ResourceNotFoundException;
import com.clothstore.product.dto.ProductRequest;
import com.clothstore.product.dto.ProductResponse;
import com.clothstore.product.entity.Product;
import com.clothstore.product.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public ProductResponse addProduct(ProductRequest product) {

        Product productEntity = new Product();

        productEntity.setName(product.getName());
        productEntity.setDescription(product.getDescription());
        productEntity.setActive(true);

        return convertToProductResponse(productRepo.save(productEntity));
    }

    public List<ProductResponse> getAllProducts() {

        List<Product> productList = productRepo.findAllByActiveTrue();

        return convertToProductResponse(productList);
    }

    public ProductResponse getProductById(Long id) {

        Product existingProduct = productRepo.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        return convertToProductResponse(existingProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest product) {

        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());

        return convertToProductResponse(productRepo.save(existingProduct));
    }

    public ProductResponse deleteProduct(Long id) {

        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        existingProduct.setActive(false);

        return convertToProductResponse(productRepo.save(existingProduct));
    }

    private ProductResponse convertToProductResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setActive(product.isActive());
        response.setCreatedDate(product.getCreatedDate());
        response.setUpdatedDate(product.getUpdatedDate());

        return response;
    }

    private List<ProductResponse> convertToProductResponse(List<Product> productList) {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : productList) {
            productResponses.add(convertToProductResponse(product));
        }
        return productResponses;
    }
}