package com.clothstore.product.repository;

import com.clothstore.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepo extends JpaRepository<ProductVariant, Long> {

}
