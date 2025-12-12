package com.ucop.controller;

import com.ucop.Dao.CartItemDAO;
import com.ucop.entity.Cart;
import com.ucop.entity.Order;
import com.ucop.service.OrderService;

/**
 * Controller for Cart operations
 */
public class CartController {

    private OrderService orderService;
    
    public CartController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get or create cart for customer
     */
    public Cart getOrCreateCart(Long accountId) {
        try {
            return orderService.getOrCreateCart(accountId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add item to cart
     */
    public void addToCart(Long cartId, CartItemDAO itemDTO) {
        try {
            orderService.addToCart(cartId, itemDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(Long cartId, Long itemId) {
        try {
            orderService.removeFromCart(cartId, itemId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update cart item quantity
     */
    public void updateQuantity(Long cartId, Long itemId, int quantity) {
        try {
            orderService.updateCartItemQuantity(cartId, itemId, quantity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Place order from cart
     */
    public Order placeOrder(Long cartId) {
        try {
            return orderService.placeOrder(cartId, Order.OrderStatus.PLACED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
