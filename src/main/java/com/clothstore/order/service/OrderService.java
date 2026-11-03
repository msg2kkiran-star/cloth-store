package com.clothstore.order.service;

import com.clothstore.cart.entity.Cart;
import com.clothstore.cart.entity.CartItem;
import com.clothstore.cart.repository.CartRepo;
import com.clothstore.customer.address.entity.CustomerAddress;
import com.clothstore.customer.address.repository.CustomerAddressRepo;
import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;
import com.clothstore.exception.ResourceNotFoundException;
import com.clothstore.order.dto.OrderItemResponse;
import com.clothstore.order.dto.OrderResponse;
import com.clothstore.order.dto.PlaceOrderRequest;
import com.clothstore.order.entity.Order;
import com.clothstore.order.entity.OrderItem;
import com.clothstore.order.entity.OrderStatus;
import com.clothstore.order.repository.OrderRepo;
import com.clothstore.product.entity.Product;
import com.clothstore.product.entity.ProductVariant;
import com.clothstore.product.repository.ProductRepo;
import com.clothstore.product.repository.ProductVariantRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.desktop.OpenFilesEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    private final CartRepo cartRepo;
    private final CustomerRepo customerRepo;
    private final CustomerAddressRepo customerAddressRepo;
    private final ProductVariantRepo productVariantRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    public OrderService(CartRepo cartRepo, CustomerRepo customerRepo, CustomerAddressRepo customerAddressRepo,
                        ProductVariantRepo productVariantRepo, ProductRepo productRepo, OrderRepo orderRepo) {
        this.cartRepo = cartRepo;
        this.customerRepo = customerRepo;
        this.customerAddressRepo = customerAddressRepo;
        this.productVariantRepo = productVariantRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    @Transactional
    public OrderResponse placeOrder(Long customerId, PlaceOrderRequest request) {

        Customer customer = customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        CustomerAddress customerAddress = customerAddressRepo.findByIdAndCustomerIdAndActiveTrue(request.getAddressId(), customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + request.getAddressId() + " and customerId: " + customerId));

        Cart cart = cartRepo.findByCustomerIdAndActiveTrueWithLock(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found for customer id: " + customerId));

        List<CartItem> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        cartItems.sort(Comparator.comparing(cartItem -> cartItem.getProductVariant().getId()));

        Order order = new Order();

        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setRecipientName(customerAddress.getRecipientName());
        order.setAddressLine1(customerAddress.getAddressLine1());
        order.setAddressLine2(customerAddress.getAddressLine2());
        order.setCity(customerAddress.getCity());
        order.setState(customerAddress.getState());
        order.setPostalCode(customerAddress.getPostalCode());
        order.setCountry(customerAddress.getCountry());
        order.setPhoneNumber(customerAddress.getPhoneNumber());
        order.setLandmark(customerAddress.getLandmark());

        for (CartItem cartItem : cartItems) {

            ProductVariant productVariant = productVariantRepo.findByIdAndActiveTrueWithLock(cartItem.getProductVariant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + cartItem.getProductVariant().getId()));

            Product product = productRepo.findByIdAndActiveTrue(productVariant.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productVariant.getProduct().getId()));

            if (cartItem.getQuantity() > productVariant.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock. Available: " + productVariant.getStockQuantity()
                                + ", Requested: " + cartItem.getQuantity()
                );
            }

            OrderItem orderItem = prepareOrderItem(order, productVariant, product, cartItem);
            order.addItem(orderItem);
            order.setTotalAmount(order.getTotalAmount().add(orderItem.getTotalPrice()));
        }
        order.setOrderNumber(generateOrderNumber());
        orderRepo.save(order);
        cart.removeAllItems();
        return prepareOrderResponse(order);
    }

    private String generateOrderNumber() {
        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

        final String datePart = LocalDate.now().format(DATE_FORMATTER);

        final String randomPart = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "ORD-" + datePart + "-" + randomPart;
    }

    private OrderResponse prepareOrderResponse(Order order) {

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setOrderStatus(order.getOrderStatus());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setRecipientName(order.getRecipientName());
        orderResponse.setPhoneNumber(order.getPhoneNumber());
        orderResponse.setAddressLine1(order.getAddressLine1());
        orderResponse.setAddressLine2(order.getAddressLine2());
        orderResponse.setLandmark(order.getLandmark());
        orderResponse.setCity(order.getCity());
        orderResponse.setState(order.getState());
        orderResponse.setPostalCode(order.getPostalCode());
        orderResponse.setCountry(order.getCountry());
        orderResponse.setOrderItems(prepareOrderItemResponse(order.getOrderItems()));
        orderResponse.setCreatedDate(order.getCreatedDate());
        orderResponse.setUpdatedDate(order.getUpdatedDate());


        return orderResponse;
    }

    private List<OrderItemResponse> prepareOrderItemResponse(List<OrderItem> orderItems) {
        List<OrderItemResponse> orderItemResponseList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setId(orderItem.getId());
            orderItemResponse.setProductVariantId(orderItem.getProductVariant().getId());
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponse.setProductName(orderItem.getProductName());
            orderItemResponse.setProductColor(orderItem.getProductColor());
            orderItemResponse.setProductSize(orderItem.getProductSize());
            orderItemResponse.setSku(orderItem.getSku());
            orderItemResponse.setUnitPrice(orderItem.getUnitPrice());
            orderItemResponse.setTotalPrice(orderItem.getTotalPrice());
            orderItemResponseList.add(orderItemResponse);
        }
        return orderItemResponseList;
    }

    private OrderItem prepareOrderItem(Order order, ProductVariant productVariant, Product product, CartItem cartItem) {

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setProductVariant(productVariant);
        orderItem.setProductName(product.getName());
        orderItem.setProductSize(productVariant.getProductSize());
        orderItem.setProductColor(productVariant.getProductColor());
        orderItem.setSku(productVariant.getSku());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setUnitPrice(productVariant.getPrice());
        orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
//        orderItemRepo.save(orderItem);

        productVariant.setStockQuantity(productVariant.getStockQuantity() - orderItem.getQuantity());
//        productVariantRepo.save(productVariant);

        return orderItem;
    }

    public List<OrderResponse> getAllOrdersOfCustomer(Long customerId) {
        List<Order> orderList = orderRepo.findAllByCustomerId(customerId);
        List<OrderResponse> orderResponseList = new ArrayList<>();

        for (Order order : orderList) {
            orderResponseList.add(prepareOrderResponse(order));
        }
        return orderResponseList;
    }

    public OrderResponse getOrderById(Long orderId, Long customerId) {
        Order order = orderRepo.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order id : " + orderId + " not found!" + "for CustomerId: " + customerId));
        return prepareOrderResponse(order);
    }
}
