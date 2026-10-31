package com.clothstore.cart.repository;

import com.clothstore.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    boolean existsByCartIdAndProductVariantId(Long cartId, Long productVariantId);

    Optional<CartItem> findByIdAndCartId(Long cartId, Long cartItemId);
}
