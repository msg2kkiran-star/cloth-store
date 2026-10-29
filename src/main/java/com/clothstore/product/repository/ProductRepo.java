package com.clothstore.product.repository;

import com.clothstore.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findAllByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(Long id);

}
