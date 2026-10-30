
package com.clothstore.customer.controller;

import com.clothstore.customer.dto.CustomerRequest;
import com.clothstore.customer.dto.CustomerResponse;
import com.clothstore.customer.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Create customer
    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CustomerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    // Get all active customers
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAll() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Get customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getById(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                customerService.getCustomerById(customerId)
        );
    }

    // Update customer
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest request) {

        return ResponseEntity.ok(
                customerService.updateCustomer(customerId, request)
        );
    }

    // Soft delete customer
    @DeleteMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> delete(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                customerService.deleteCustomer(customerId)
        );
    }
}
