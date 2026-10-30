
package com.clothstore.customer.address.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressResponse {

    private Long id;
    private Long customerId;

    private String recipientName;
    private String phoneNumber;

    private String addressLine1;
    private String addressLine2;
    private String landmark;

    private String city;
    private String state;
    private String postalCode;
    private String country;

    private boolean defaultAddress;
    private boolean active;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
