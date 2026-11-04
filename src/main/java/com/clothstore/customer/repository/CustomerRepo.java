package com.clothstore.customer.repository;

import com.clothstore.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    List<Customer> findAllByActiveTrue();

    Optional<Customer> findByIdAndActiveTrue(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Customer findByEmail(String email);
}
