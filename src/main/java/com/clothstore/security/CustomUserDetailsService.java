package com.clothstore.security;

import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    public CustomUserDetailsService(
            CustomerRepo customerRepo,
            PasswordEncoder passwordEncoder) {

        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Check ADMIN first
        if (adminUsername.equalsIgnoreCase(username.trim())) {

            return User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles("ADMIN")
                    .build();
        }

        // Otherwise, find CUSTOMER by email
        Customer customer = customerRepo
                .findByEmailAndActiveTrue(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username
                ));

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .roles("CUSTOMER")
                .build();
    }
}