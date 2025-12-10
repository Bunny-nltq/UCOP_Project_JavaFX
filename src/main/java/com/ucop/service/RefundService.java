package com.ucop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.ucop.entity.Order;
import com.ucop.entity.Payment;
import com.ucop.entity.Refund;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.PaymentRepository;
import com.ucop.repository.RefundRepository;

/**
 * Service for refund processing
 */
public class RefundService {
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public RefundService(RefundRepository refundRepository,
                         PaymentRepository paymentRepository,
                         OrderRepository orderRepository) {
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Create refund for payment (partial or full)
     */
    public Refund createRefund(Long paymentId, BigDecimal amount, Refund.RefundType refundType, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }

        Payment payment = paymentOpt.get();

        // Validate refund amount
        if (refundType == Refund.RefundType.FULL) {
            amount = payment.getAmount();
        } else if (refundType == Refund.RefundType.PARTIAL) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(payment.getAmount()) > 0) {
                throw new IllegalArgumentException("Invalid refund amount for partial refund");
            }
        }

        Refund refund = new Refund(payment, amount, refundType);
        refund.setReason(reason);
        refund.setStatus(Refund.RefundStatus.PENDING);
        refund.setCreatedAt(LocalDateTime.now());

        return refundRepository.save(refund);
    }

    /**
     * Process refund (simulate gateway processing)
     */
    public Refund processRefund(Long refundId, boolean success) {
        Optional<Refund> refundOpt = refundRepository.findById(refundId);
        if (refundOpt.isEmpty()) {
            throw new IllegalArgumentException("Refund not found");
        }

        Refund refund = refundOpt.get();

        if (success) {
            refund.setStatus(Refund.RefundStatus.SUCCESS);
            refund.setRefundedAt(LocalDateTime.now());
            refund.setRefundTransactionId(generateRefundTransactionId());

            // Update order status if full refund
            if (refund.getRefundType() == Refund.RefundType.FULL) {
                Order order = refund.getPayment().getOrder();
                order.setStatus(Order.OrderStatus.REFUNDED);
                orderRepository.update(order);
            }
        } else {
            refund.setStatus(Refund.RefundStatus.FAILED);
        }

        refund.setUpdatedAt(LocalDateTime.now());
        refundRepository.update(refund);
        return refund;
    }

    /**
     * Get refund by ID
     */
    public Optional<Refund> getRefundById(Long refundId) {
        return refundRepository.findById(refundId);
    }

    /**
     * Get refunds for payment
     */
    public List<Refund> getRefundsByPaymentId(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId);
    }

    /**
     * Get refunds by status
     */
    public List<Refund> getRefundsByStatus(Refund.RefundStatus status) {
        return refundRepository.findByStatus(status.name());
    }

    /**
     * Calculate total refunded amount for a payment
     */
    public BigDecimal calculateTotalRefunded(Long paymentId) {
        List<Refund> refunds = refundRepository.findByPaymentId(paymentId);
        return refunds.stream()
                .filter(r -> r.getStatus() == Refund.RefundStatus.SUCCESS)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Generate unique refund transaction ID
     */
    private String generateRefundTransactionId() {
        return "REFUND-" + System.currentTimeMillis() + "-" + (int)(ThreadLocalRandom.current().nextDouble() * 10000);
    }
}
