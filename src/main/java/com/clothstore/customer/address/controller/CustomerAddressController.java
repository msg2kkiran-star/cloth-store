package com.clothstore.customer.address.controller;

import com.clothstore.customer.address.dto.AddressRequest;
import com.clothstore.customer.address.dto.AddressResponse;
import com.clothstore.customer.address.service.CustomerAddressService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@PreAuthorize("@customerAuthorization.canAccessCustomer(#p0, authentication)")
public class CustomerAddressController {

    private final CustomerAddressService addressService;

    public CustomerAddressController(CustomerAddressService addressService) {

        this.addressService = addressService;
    }

    // Create address
    @PostMapping
    public ResponseEntity<AddressResponse> create(@PathVariable Long customerId, @Valid @RequestBody AddressRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(customerId, request));
    }

    // Get all addresses
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAll(@PathVariable Long customerId) {

        return ResponseEntity.ok(addressService.getAllAddresses(customerId));
    }

    // Get address by ID
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getById(@PathVariable Long customerId, @PathVariable Long addressId) {

        return ResponseEntity.ok(addressService.getAddressById(customerId, addressId));
    }

    // Update address
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long customerId, @PathVariable Long addressId, @Valid @RequestBody AddressRequest request) {

        return ResponseEntity.ok(addressService.updateAddress(customerId, addressId, request));
    }

    // Set default address
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefault(@PathVariable Long customerId, @PathVariable Long addressId) {

        return ResponseEntity.ok(addressService.setDefaultAddress(customerId, addressId));
    }

    // Soft delete address
    @DeleteMapping("/{addressId}")
    public ResponseEntity<AddressResponse> delete(@PathVariable Long customerId, @PathVariable Long addressId) {

        return ResponseEntity.ok(addressService.deleteAddress(customerId, addressId));
    }
}
