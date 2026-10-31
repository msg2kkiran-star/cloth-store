package com.clothstore.cart.repository;

import com.clothstore.cart.entity.Cart;
import com.clothstore.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerIdAndActiveTrue(Long customerId);
    List<CartItem> findAllByIdAndActiveTrue(Long id);
}
