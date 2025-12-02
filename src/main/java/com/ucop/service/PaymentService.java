package com.ucop.service;

import com.ucop.entity.*;
import com.ucop.dto.PaymentCalculationDTO;
import com.ucop.repository.*;
import com.ucop.util.PaymentCalculator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for payment processing and calculation
 */
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private static final BigDecimal WALLET_COMMISSION_RATE = new BigDecimal("0.01"); // 1%

    public PaymentService(PaymentRepository paymentRepository,
                          RefundRepository refundRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Calculate payment breakdown for an order
     */
    public PaymentCalculationDTO calculatePayment(BigDecimal subtotal, 
                                                   BigDecimal itemDiscount,
                                                   BigDecimal cartDiscount,
                                                   Payment.PaymentMethod paymentMethod) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Subtotal must be greater than zero");
        }

        // Ensure discounts are valid
        if (itemDiscount == null) itemDiscount = BigDecimal.ZERO;
        if (cartDiscount == null) cartDiscount = BigDecimal.ZERO;

        // Calculate individual components
        BigDecimal taxAmount = PaymentCalculator.calculateTax(subtotal);
        BigDecimal shippingFee = PaymentCalculator.calculateShippingFee(subtotal);
        
        BigDecimal codFee = BigDecimal.ZERO;
        BigDecimal gatewayFee = BigDecimal.ZERO;

        // Calculate method-specific fees
        if (paymentMethod == Payment.PaymentMethod.COD) {
            codFee = PaymentCalculator.calculateCODFee(subtotal);
        } else if (paymentMethod == Payment.PaymentMethod.GATEWAY) {
            gatewayFee = PaymentCalculator.calculateGatewayFee(subtotal);
        } else if (paymentMethod == Payment.PaymentMethod.WALLET) {
            // Wallet has commission
            gatewayFee = subtotal.multiply(WALLET_COMMISSION_RATE);
        }

        return new PaymentCalculationDTO(
                subtotal,
                itemDiscount,
                cartDiscount,
                taxAmount,
                shippingFee,
                codFee,
                gatewayFee
        );
    }

    /**
     * Create payment for order
     */
    public Payment createPayment(Long orderId, Payment.PaymentMethod method, BigDecimal amount) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOpt.get();
        Payment payment = new Payment(order, method, amount);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Process payment (simulate gateway processing)
     */
    public Payment processPayment(Long paymentId, boolean success) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        
        if (success) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }

        payment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.update(payment);
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.update(payment);
    }

    /**
     * Get payment by ID
     */
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    /**
     * Get payments for order
     */
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    /**
     * Get payments by status
     */
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status.name());
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}
