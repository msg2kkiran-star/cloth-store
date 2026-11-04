
package com.clothstore.customer.service;

import com.clothstore.customer.dto.CustomerRequest;
import com.clothstore.customer.dto.CustomerResponse;
import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;
import com.clothstore.exception.DuplicateResourceException;
import com.clothstore.exception.ResourceNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepo customerRepo,  PasswordEncoder passwordEncoder) {
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Create customer
    public CustomerResponse createCustomer(CustomerRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (customerRepo.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Customer already exists with email: " + email
            );
        }

        Customer customer = new Customer();

        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setPhoneNumber(request.getPhoneNumber().trim());

        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        return convertToResponse(customerRepo.save(customer));
    }

    // Get all active customers
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        return convertToResponse(customerRepo.findAllByActiveTrue());
    }

    // Get customer by ID
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {

        Customer customer = customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId
                ));

        return convertToResponse(customer);
    }

    // Update customer
    public CustomerResponse updateCustomer(
            Long customerId,
            CustomerRequest request) {

        Customer existingCustomer = customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId
                ));

        String email = normalizeEmail(request.getEmail());

        if (customerRepo.existsByEmailAndIdNot(email, customerId)) {
            throw new DuplicateResourceException(
                    "Customer already exists with email: " + email
            );
        }

        existingCustomer.setFirstName(request.getFirstName().trim());
        existingCustomer.setLastName(request.getLastName().trim());
        existingCustomer.setEmail(email);
        existingCustomer.setPhoneNumber(request.getPhoneNumber().trim());

        return convertToResponse(customerRepo.save(existingCustomer));
    }

    // Soft delete customer
    public CustomerResponse deleteCustomer(Long customerId) {

        Customer existingCustomer = customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId
                ));

        existingCustomer.setActive(false);

        return convertToResponse(customerRepo.save(existingCustomer));
    }

    // Normalize email
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Convert entity to response DTO
    private CustomerResponse convertToResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setActive(customer.isActive());
        response.setCreatedDate(customer.getCreatedDate());
        response.setUpdatedDate(customer.getUpdatedDate());

        return response;
    }

    // Convert list of entities to list of response DTOs
    private List<CustomerResponse> convertToResponse(
            List<Customer> customers) {

        List<CustomerResponse> responses = new ArrayList<>();

        for (Customer customer : customers) {
            responses.add(convertToResponse(customer));
        }

        return responses;
    }
}
