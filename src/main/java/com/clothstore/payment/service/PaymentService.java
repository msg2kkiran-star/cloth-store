package com.clothstore.payment.service;

import com.clothstore.customer.repository.CustomerRepo;
import com.clothstore.exception.BadRequestException;
import com.clothstore.exception.DuplicateResourceException;
import com.clothstore.exception.ResourceNotFoundException;
import com.clothstore.order.entity.Order;
import com.clothstore.order.entity.OrderStatus;
import com.clothstore.order.repository.OrderRepo;
import com.clothstore.payment.dto.CreatePaymentRequest;
import com.clothstore.payment.dto.PaymentResponse;
import com.clothstore.payment.entity.Payment;
import com.clothstore.payment.entity.PaymentMethod;
import com.clothstore.payment.entity.PaymentStatus;
import com.clothstore.payment.repository.PaymentRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;

    public PaymentService(PaymentRepo paymentRepo, OrderRepo orderRepo, CustomerRepo customerRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }

    @Transactional
    public PaymentResponse createPayment(Long customerId, Long orderId, CreatePaymentRequest createPaymentRequest) {

        Order order = orderRepo.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id : " + orderId + " not found!" + "for CustomerId: " + customerId));

        paymentRepo.findByOrderId(orderId).ifPresent(existingPayment -> {
            throw new DuplicateResourceException(
                    "Payment already exists with status: " + existingPayment.getPaymentStatus());
        });

        if (createPaymentRequest.getPaymentMethod() != PaymentMethod.COD) {
            throw new BadRequestException("Currently only COD payment is supported");
        }

        Payment payment = new Payment();
        payment.setAmount(order.getTotalAmount());
        payment.setOrder(order);
        payment.setPaymentMethod(createPaymentRequest.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepo.save(payment);

        order.setOrderStatus(OrderStatus.CONFIRMED);

        return preparePaymentResponse(payment);
    }

    private PaymentResponse preparePaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(payment.getId());
        paymentResponse.setOrderId(payment.getOrder().getId());
        paymentResponse.setPaymentMethod(payment.getPaymentMethod());
        paymentResponse.setPaymentStatus(payment.getPaymentStatus());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setTransactionId(payment.getTransactionId());
        paymentResponse.setCreatedDate(payment.getCreatedDate());
        paymentResponse.setUpdatedDate(payment.getUpdatedDate());
        paymentResponse.setPaidDate(payment.getPaidDate());

        return paymentResponse;
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentDetails(Long customerId, Long orderId) {

        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        orderRepo.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id : " + orderId + " not found!" + "for CustomerId: " + customerId));

        Optional<Payment> payment = paymentRepo.findByOrderId(orderId);

        if (payment.isPresent()) {
            return preparePaymentResponse(payment.get());
        } else {
            throw new ResourceNotFoundException("Payment details not found");
        }
    }

    @Transactional
    public PaymentResponse updateDetailsAfterDelivery(Long customerId, Long orderId) {
        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Order order = orderRepo.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id : " + orderId + " not found!" + "for CustomerId: " + customerId));

        Optional<Payment> payment = paymentRepo.findByOrderId(orderId);

        if (payment.isPresent()) {

            if (payment.get().getPaymentMethod() != PaymentMethod.COD) {
                throw new BadRequestException("This operation is only supported for COD payments");
            }

            if (payment.get().getPaymentStatus() != PaymentStatus.PENDING) {
                throw new BadRequestException("Payment is not in PENDING status");
            }

            payment.get().setPaymentStatus(PaymentStatus.SUCCESS);
            payment.get().setPaidDate();
            Payment paymentDone = paymentRepo.save(payment.get());

            order.setOrderStatus(OrderStatus.DELIVERED);

            return preparePaymentResponse(paymentDone);
        } else {
            throw new ResourceNotFoundException("Payment details not found");
        }
    }
}
