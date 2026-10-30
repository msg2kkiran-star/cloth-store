
package com.clothstore.customer.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    @NotBlank
    @Size(max = 100)
    private String recipientName;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 150)
    private String addressLine1;

    @Size(max = 150)
    private String addressLine2;

    @Size(max = 100)
    private String landmark;

    @NotBlank
    @Size(max = 80)
    private String city;

    @NotBlank
    @Size(max = 80)
    private String state;

    @NotBlank
    @Size(max = 15)
    private String postalCode;

    @NotBlank
    @Size(max = 60)
    private String country;
}