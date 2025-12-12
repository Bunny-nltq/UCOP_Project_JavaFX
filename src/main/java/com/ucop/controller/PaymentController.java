package com.ucop.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.ucop.Dao.PaymentCalculationDAO;
import com.ucop.entity.Payment;
import com.ucop.service.PaymentService;

/**
 * Controller for Payment operations
 */
public class PaymentController {

    private PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Calculate payment breakdown
     */
    public PaymentCalculationDAO calculatePayment(BigDecimal subtotal,
                                             BigDecimal itemDiscount,
                                             BigDecimal cartDiscount,
                                             String paymentMethod) {
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);
            return paymentService.calculatePayment(subtotal, itemDiscount, cartDiscount, method);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create payment
     */
    public Payment createPayment(Long orderId,
                                 String paymentMethod,
                                 BigDecimal amount) {
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);
            return paymentService.createPayment(orderId, method, amount);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process payment
     */
    public Payment processPayment(Long paymentId, boolean success) {
        try {
            return paymentService.processPayment(paymentId, success);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get payment by ID
     */
    public Optional<Payment> getPayment(Long paymentId) {
        try {
            return paymentService.getPaymentById(paymentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get payments by order
     */
    public List<Payment> getPaymentsByOrder(Long orderId) {
        try {
            return paymentService.getPaymentsByOrderId(orderId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus(Long paymentId, String status) {
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
            paymentService.updatePaymentStatus(paymentId, paymentStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment status", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}