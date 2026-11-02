package com.clothstore.order.entity;

import com.clothstore.customer.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PLACED;

    @Column(nullable = false, precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime updatedDate;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String recipientName;

    @NotBlank
    @Column(nullable = false, length = 20)
    @Size(max = 20)
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    List<OrderItem> orderItems = new ArrayList<>();

    @PrePersist
    public void setDatesBeforePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate
    public void setDatesAfterUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        this.orderItems.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        if (this.orderItems.remove(item)) {
            item.setOrder(null);
        }
    }

    public void removeAllItems() {
        for (OrderItem item : orderItems) {
            item.setOrder(null);
        }
        this.orderItems.clear();
    }
}
