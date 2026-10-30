
package com.clothstore.customer.address.entity;

import com.clothstore.customer.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customer_addresses")
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String recipientName;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String addressLine1;

    @Size(max = 150)
    private String addressLine2;

    @Size(max = 100)
    private String landmark;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String city;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String state;

    @NotBlank
    @Size(max = 15)
    @Column(nullable = false, length = 15)
    private String postalCode;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String country;

    @Column(nullable = false)
    private boolean defaultAddress = false;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
