package com.clothstore.product.service;

import com.clothstore.exception.ResourceNotFoundException;
import com.clothstore.product.entity.Product;
import com.clothstore.product.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAllByActiveTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findByIdAndActiveTrue(id);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setActive(product.isActive());

        productRepo.save(existingProduct);

        return existingProduct;
    }

    public Product deleteProduct(Long id) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        existingProduct.setActive(false);

        return productRepo.save(existingProduct);
    }
}
