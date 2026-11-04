package com.clothstore.security;

import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("customerAuthorization")
public class CustomerAuthorization {

    private final CustomerRepo customerRepo;

    public CustomerAuthorization(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    public boolean canAccessCustomer(
            Long customerId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admin can access any customer's data
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        // Customer can access only their own data
        String email = authentication.getName()
                .trim()
                .toLowerCase(Locale.ROOT);

        return customerRepo.findByEmailAndActiveTrue(email)
                .map(Customer::getId)
                .filter(customerId::equals)
                .isPresent();
    }
}