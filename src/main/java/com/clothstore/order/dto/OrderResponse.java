package com.clothstore.order.dto;

import com.clothstore.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;
    private String orderNumber;

    private OrderStatus orderStatus;
    private BigDecimal totalAmount;

    // Shipping address snapshot
    private String recipientName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private List<OrderItemResponse> orderItems;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}