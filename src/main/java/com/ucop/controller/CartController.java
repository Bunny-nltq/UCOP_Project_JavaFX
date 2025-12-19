package com.ucop.controller;

import com.ucop.dao.CartItemDAO;
import com.ucop.entity.Cart;
import com.ucop.entity.Order;
import com.ucop.service.OrderService;

/**
 * Controller for Cart operations
 */
public class CartController {

    private final OrderService orderService;

    public CartController(OrderService orderService) {
        if (orderService == null) {
            throw new IllegalArgumentException("OrderService cannot be null");
        }
        this.orderService = orderService;
    }

    /**
     * Get or create cart for customer
     */
    public Cart getOrCreateCart(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }
        try {
            return orderService.getOrCreateCart(accountId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get or create cart for accountId: " + accountId, e);
        }
    }

    /**
     * Add item to cart
     */
    public void addToCart(Long cartId, CartItemDAO itemDTO) {
        if (cartId == null || itemDTO == null) {
            throw new IllegalArgumentException("cartId and itemDTO cannot be null");
        }
        try {
            orderService.addToCart(cartId, itemDTO);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to add itemId " + itemDTO.getItemId() + " to cart " + cartId, e
            );
        }
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(Long cartId, Long itemId) {
        if (cartId == null || itemId == null) {
            throw new IllegalArgumentException("cartId and itemId cannot be null");
        }
        try {
            orderService.removeFromCart(cartId, itemId);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to remove itemId " + itemId + " from cart " + cartId, e
            );
        }
    }

    /**
     * Update cart item quantity
     */
    public void updateQuantity(Long cartId, Long itemId, int quantity) {
        if (cartId == null || itemId == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid parameters for updateQuantity");
        }
        try {
            orderService.updateCartItemQuantity(cartId, itemId, quantity);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to update quantity for itemId " + itemId + " in cart " + cartId, e
            );
        }
    }

    /**
     * Place order from cart
     */
    public Order placeOrder(Long cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("cartId cannot be null");
        }
        try {
            return orderService.placeOrder(cartId, Order.OrderStatus.PLACED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to place order for cartId: " + cartId, e);
        }
    }
}
