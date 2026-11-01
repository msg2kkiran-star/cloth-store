package com.clothstore.cart.service;

import com.clothstore.cart.dto.CartItemResponse;
import com.clothstore.cart.dto.CartResponse;
import com.clothstore.cart.entity.Cart;
import com.clothstore.cart.entity.CartItem;
import com.clothstore.cart.repository.CartItemRepo;
import com.clothstore.cart.repository.CartRepo;
import com.clothstore.customer.entity.Customer;
import com.clothstore.customer.repository.CustomerRepo;
import com.clothstore.exception.ResourceNotFoundException;
import com.clothstore.product.entity.ProductVariant;
import com.clothstore.product.repository.ProductRepo;
import com.clothstore.product.repository.ProductVariantRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;
    private final ProductVariantRepo productVariantRepo;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo, CustomerRepo customerRepo, ProductRepo productRepo,
                       ProductVariantRepo productVariantRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.productVariantRepo = productVariantRepo;
    }

    public Cart getOrCreateCart(Long customerId) {

        Customer customer = customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Optional<Cart> existingCart = cartRepo.findByCustomerIdAndActiveTrue(customerId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setActive(true);
        return cartRepo.save(cart);
    }

    public CartResponse addItemToCart(Long customerId, Long productVariantId, Integer quantity) {

        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        ProductVariant productVariant = productVariantRepo.findByIdAndActiveTrue(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + productVariantId));

        productRepo.findByIdAndActiveTrue(productVariant.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productVariant.getProduct().getId()));

        if (quantity != null && quantity > 0) {
            if (quantity <= productVariant.getStockQuantity()) {
                Cart cart = getOrCreateCart(customerId);
                for (CartItem item : cart.getItems()) {
                    if (item.getProductVariant().getId().equals(productVariant.getId())) {
                        Integer quantityToAdd = item.getQuantity() + quantity;
                        if (quantityToAdd <= productVariant.getStockQuantity()) {
                            item.setQuantity(quantityToAdd);
                            cartItemRepo.save(item);
                            return convertToCartResponse(cart, customerId);
                        } else {
                            throw new IllegalArgumentException("Extremely sorry we have only " + productVariant.getStockQuantity() + " stock products");
                        }
                    }
                }
                CartItem item = new CartItem();
                item.setProductVariant(productVariant);
                item.setQuantity(quantity);
                item.setCart(cart);
                cart.addItem(item);
                cartItemRepo.save(item);
                cartRepo.save(cart);
                return convertToCartResponse(cart, customerId);
            } else {
                throw new IllegalArgumentException("Extremely sorry we have only " + productVariant.getStockQuantity() + " stock products");
            }
        } else {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long customerId) {

        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Optional<Cart> existingCart = cartRepo.findByCustomerIdAndActiveTrue(customerId);

        if (existingCart.isPresent()) {
            return convertToCartResponse(existingCart.get(), customerId);
        }
        Cart cart = new Cart();
        cart.setCustomer(null);
        return convertToCartResponse(cart, customerId);
    }

    private CartResponse convertToCartResponse(Cart cart, Long customerId) {
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                BigDecimal unitPrice = item.getProductVariant().getPrice();
                BigDecimal totalPrice = BigDecimal.valueOf(item.getQuantity()).multiply(unitPrice);
                grandTotal = grandTotal.add(totalPrice);
                cartItemResponses.add(convertToCartItemResponse(item, totalPrice));
            }
        }

        CartResponse cartResponse = new CartResponse();
        cartResponse.setId(cart.getId());
        cartResponse.setCustomerId(customerId);
        cartResponse.setItems(cartItemResponses);
        cartResponse.setGrandTotal(grandTotal);

        return cartResponse;
    }

    private CartItemResponse convertToCartItemResponse(CartItem item, BigDecimal totalPrice) {

        CartItemResponse cartItemResponse = new CartItemResponse();

        cartItemResponse.setItemId(item.getId());
        cartItemResponse.setProductVariantId(item.getProductVariant().getId());
        cartItemResponse.setProductName(item.getProductVariant().getProduct().getName());
        cartItemResponse.setProductSize(item.getProductVariant().getProductSize());
        cartItemResponse.setProductColor(item.getProductVariant().getProductColor());
        cartItemResponse.setQuantity(item.getQuantity());
        cartItemResponse.setUnitPrice(item.getProductVariant().getPrice());
        cartItemResponse.setTotalPrice(totalPrice);

        return cartItemResponse;
    }

    public CartResponse updateCartItemQuantity(Long customerId, Long itemId, Integer quantity) {

        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Optional<Cart> existingCart = cartRepo.findByCustomerIdAndActiveTrue(customerId);

        if (existingCart.isPresent()) {
            Optional<CartItem> itemToUpdate = cartItemRepo.findByIdAndCartId(itemId, existingCart.get().getId());
            if (itemToUpdate.isPresent()) {
                ProductVariant productVariant = itemToUpdate.get().getProductVariant();

                productVariantRepo.findByIdAndActiveTrue(productVariant.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + productVariant.getId()));

                productRepo.findByIdAndActiveTrue(productVariant.getProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productVariant.getProduct().getId()));

                if (quantity != null && quantity <= productVariant.getStockQuantity()) {
                    itemToUpdate.get().setQuantity(quantity);
                    cartItemRepo.save(itemToUpdate.get());
                    return convertToCartResponse(existingCart.get(), customerId);
                } else {
                    throw new ResourceNotFoundException("Extremely sorry we have only " + productVariant.getStockQuantity() + " stock products");
                }
            } else {
                throw new ResourceNotFoundException("Provided Item not found in cart id: " + existingCart.get().getId());
            }
        } else {
            throw new ResourceNotFoundException("Active cart not found for customer id: " + customerId);
        }
    }

    public void deleteCartItem(Long customerId, Long itemId) {
        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Optional<Cart> existingCart = cartRepo.findByCustomerIdAndActiveTrue(customerId);
        if (existingCart.isPresent()) {
            Optional<CartItem> itemToDelete = cartItemRepo.findByIdAndCartId(itemId, existingCart.get().getId());
            if (itemToDelete.isPresent()) {
//                cartItemRepo.delete(itemToDelete.get());
                existingCart.get().removeItem(itemToDelete.get());
            } else
                throw new ResourceNotFoundException("Provided Item not found in cart id: " + existingCart.get().getId());
        } else throw new ResourceNotFoundException("Active cart not found for customer: " + customerId);
    }

    public void clearCart(Long customerId) {
        customerRepo.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Optional<Cart> existingCart = cartRepo.findByCustomerIdAndActiveTrue(customerId);
        if (existingCart.isPresent()) {
            existingCart.get().removeAllItems();
        } else throw new ResourceNotFoundException("Active cart not found for customer: " + customerId);
    }
}
