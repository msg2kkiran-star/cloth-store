package com.clothstore.security;

import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerRepo customerRepo;

    public CustomerDetailsService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepo.findByEmail(username);

        if (customer == null || !customer.isActive()) {
            throw new UsernameNotFoundException("Customer not found with id: " + username);
        }
        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .authorities("ROLE_CUSTOMER")
                .build();
    }
}
