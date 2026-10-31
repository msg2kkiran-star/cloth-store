package com.clothstore.product.repository;

import com.clothstore.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepo extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);

    boolean existsBySku(String sku);

    Optional<ProductVariant> findByIdAndProductIdAndActiveTrue(Long id, Long productId);

    boolean existsBySkuAndIdNot(String sku, Long id);

    Optional<ProductVariant> findByIdAndActiveTrue(Long id);
}
