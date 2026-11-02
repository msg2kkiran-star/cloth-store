package com.clothstore.order.controller;

import com.clothstore.order.dto.OrderResponse;
import com.clothstore.order.dto.PlaceOrderRequest;
import com.clothstore.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeAnOrder(@PathVariable Long customerId, @RequestBody @Valid PlaceOrderRequest placeOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(customerId, placeOrderRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getAllOrdersOfCustomer(customerId));
    }
}
