package com.clothstore.customer.address.service;

import com.clothstore.customer.address.dto.AddressRequest;
import com.clothstore.customer.address.dto.AddressResponse;
import com.clothstore.customer.address.entity.CustomerAddress;
import com.clothstore.customer.address.repository.CustomerAddressRepo;
import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;
import com.clothstore.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerAddressService {

    private final CustomerAddressRepo addressRepo;
    private final CustomerRepo customerRepo;

    public CustomerAddressService(CustomerAddressRepo addressRepo, CustomerRepo customerRepo) {

        this.addressRepo = addressRepo;
        this.customerRepo = customerRepo;
    }

    // Create address
    public AddressResponse createAddress(Long customerId, AddressRequest request) {

        Customer customer = getActiveCustomer(customerId);

        List<CustomerAddress> existingAddresses = addressRepo.findAllByCustomerIdAndActiveTrue(customerId);

        CustomerAddress address = new CustomerAddress();

        address.setCustomer(customer);
        updateAddressFields(address, request);

        // First address becomes default automatically
        address.setDefaultAddress(existingAddresses.isEmpty());

        return convertToResponse(addressRepo.save(address));
    }

    // Get all active addresses of a customer
    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses(Long customerId) {

        getActiveCustomer(customerId);

        return convertToResponse(addressRepo.findAllByCustomerIdAndActiveTrue(customerId));
    }

    // Get address by ID
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long customerId, Long addressId) {

        getActiveCustomer(customerId);

        CustomerAddress address = getActiveAddress(customerId, addressId);

        return convertToResponse(address);
    }

    // Update address
    public AddressResponse updateAddress(Long customerId, Long addressId, AddressRequest request) {

        getActiveCustomer(customerId);

        CustomerAddress existingAddress = getActiveAddress(customerId, addressId);

        updateAddressFields(existingAddress, request);

        return convertToResponse(addressRepo.save(existingAddress));
    }

    // Set default address
    public AddressResponse setDefaultAddress(Long customerId, Long addressId) {

        getActiveCustomer(customerId);

        CustomerAddress selectedAddress = getActiveAddress(customerId, addressId);

        List<CustomerAddress> addresses = addressRepo.findAllByCustomerIdAndActiveTrue(customerId);

        // Only one address can be default
        for (CustomerAddress address : addresses) {

            address.setDefaultAddress(address.getId().equals(addressId));
        }

        addressRepo.saveAll(addresses);

        return convertToResponse(selectedAddress);
    }

    // Soft delete address
    public AddressResponse deleteAddress(Long customerId, Long addressId) {

        getActiveCustomer(customerId);

        CustomerAddress existingAddress = getActiveAddress(customerId, addressId);

        List<CustomerAddress> addresses = addressRepo.findAllByCustomerIdAndActiveTrue(customerId);

        // If deleting default address, select another active address
        if (existingAddress.isDefaultAddress()) {

            for (CustomerAddress address : addresses) {

                if (!address.getId().equals(addressId)) {
                    address.setDefaultAddress(true);
                    break;
                }
            }
        }

        existingAddress.setDefaultAddress(false);
        existingAddress.setActive(false);

        addressRepo.saveAll(addresses);
        addressRepo.save(existingAddress);

        return convertToResponse(existingAddress);
    }

    // Check customer exists and is active
    private Customer getActiveCustomer(Long customerId) {

        return customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    // Check address exists, belongs to customer and is active
    private CustomerAddress getActiveAddress(Long customerId, Long addressId) {

        return addressRepo.findByIdAndCustomerIdAndActiveTrue(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId + " and customerId: " + customerId));
    }

    // Update address fields
    private void updateAddressFields(CustomerAddress address, AddressRequest request) {

        address.setRecipientName(request.getRecipientName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());

        address.setAddressLine1(request.getAddressLine1().trim());
        address.setAddressLine2(trimToNull(request.getAddressLine2()));
        address.setLandmark(trimToNull(request.getLandmark()));

        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(request.getCountry().trim());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    // Entity to response DTO
    private AddressResponse convertToResponse(CustomerAddress address) {

        AddressResponse response = new AddressResponse();

        response.setId(address.getId());
        response.setCustomerId(address.getCustomer().getId());

        response.setRecipientName(address.getRecipientName());
        response.setPhoneNumber(address.getPhoneNumber());

        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setLandmark(address.getLandmark());

        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPostalCode(address.getPostalCode());
        response.setCountry(address.getCountry());

        response.setDefaultAddress(address.isDefaultAddress());
        response.setActive(address.isActive());

        response.setCreatedDate(address.getCreatedDate());
        response.setUpdatedDate(address.getUpdatedDate());

        return response;
    }

    // List of entities to list of response DTOs
    private List<AddressResponse> convertToResponse(List<CustomerAddress> addresses) {

        List<AddressResponse> responses = new ArrayList<>();

        for (CustomerAddress address : addresses) {
            responses.add(convertToResponse(address));
        }

        return responses;
    }
}
