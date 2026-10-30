package com.clothstore.customer.address.repository;

import com.clothstore.customer.address.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepo extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findAllByCustomerIdAndActiveTrue(Long customerId);

    Optional<CustomerAddress> findByIdAndCustomerIdAndActiveTrue(Long id, Long customerId);
}
