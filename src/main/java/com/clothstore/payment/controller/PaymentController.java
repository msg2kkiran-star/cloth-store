package com.clothstore.payment.controller;

import com.clothstore.payment.dto.CreatePaymentRequest;
import com.clothstore.payment.dto.PaymentResponse;
import com.clothstore.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/{customerId}/orders/{orderId}/payments")
@PreAuthorize("@customerAuthorization.canAccessCustomer(#p0, authentication)")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> doPayment(@PathVariable Long customerId, @PathVariable Long orderId, @RequestBody @Valid CreatePaymentRequest createPaymentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(customerId, orderId, createPaymentRequest));
    }

    @GetMapping
    public  ResponseEntity<PaymentResponse> getPayment(@PathVariable Long customerId, @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentDetails(customerId, orderId));
    }

    @PatchMapping
    public ResponseEntity<PaymentResponse> doDelivery(@PathVariable Long customerId, @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.updateDetailsAfterDelivery(customerId, orderId));
    }

}
