package com.ucop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ucop.dao.PaymentCalculationDAO;
import com.ucop.entity.Order;
import com.ucop.entity.Payment;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.PaymentRepository;
import com.ucop.util.PaymentCalculator;

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private static final BigDecimal WALLET_COMMISSION_RATE = new BigDecimal("0.01"); // 1%

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Calculate payment breakdown
     */
    public PaymentCalculationDAO calculatePayment(BigDecimal subtotal, 
                                                   BigDecimal itemDiscount,
                                                   BigDecimal cartDiscount,
                                                   Payment.PaymentMethod paymentMethod) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Subtotal must be greater than zero");
        }
        
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        if (itemDiscount == null) itemDiscount = BigDecimal.ZERO;
        if (cartDiscount == null) cartDiscount = BigDecimal.ZERO;
        
        // Validate discounts are non-negative
        if (itemDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Item discount cannot be negative");
        }
        if (cartDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cart discount cannot be negative");
        }

        BigDecimal taxAmount = PaymentCalculator.calculateTax(subtotal);
        BigDecimal shippingFee = PaymentCalculator.calculateShippingFee(subtotal);

        BigDecimal codFee = BigDecimal.ZERO;
        BigDecimal gatewayFee = BigDecimal.ZERO;

        // Calculate method-specific fees
        switch (paymentMethod) {
            case COD -> codFee = PaymentCalculator.calculateCODFee(subtotal);
            case BANK_TRANSFER -> gatewayFee = PaymentCalculator.calculateGatewayFee(subtotal);
            case GATEWAY -> gatewayFee = PaymentCalculator.calculateGatewayFee(subtotal);
            case WALLET -> gatewayFee = subtotal.multiply(WALLET_COMMISSION_RATE);
        }

        return new PaymentCalculationDAO(
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
     * Create payment for order with comprehensive validation
     */
    public Payment createPayment(Long orderId, Payment.PaymentMethod method, BigDecimal amount) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        
        // Validate order status for payment
        if (order.getStatus() == Order.OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot create payment for canceled order");
        }

        Payment payment = new Payment(order, method, amount);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    /**
     * Process payment (simulate gateway)
     */
    public Payment processPayment(Long paymentId, boolean success) {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }

        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentId);
        }

        Payment payment = paymentOpt.get();
        
        // Cannot process payment that's already been processed
        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS || 
            payment.getStatus() == Payment.PaymentStatus.FAILED) {
            throw new IllegalStateException("Payment has already been processed");
        }
        
        if (success) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }

        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.update(payment);
        return payment;
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException("Payment ID must be valid");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }

        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentId);
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(status);
        paymentRepository.update(payment);
    }

    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status.name());
    }

    /**
     * Generate unique transaction ID using UUID
     * This guarantees uniqueness and follows standard practices
     */
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
