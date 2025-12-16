package com.ucop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.ucop.entity.Order;
import com.ucop.entity.Payment;
import com.ucop.entity.Refund;
import com.ucop.entity.RefundStatus;
import com.ucop.entity.RefundType;
import com.ucop.repository.OrderRepository;
import com.ucop.repository.PaymentRepository;
import com.ucop.repository.RefundRepository;

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
     * Create refund for a payment
     */
    public Refund createRefund(Long paymentId,
                               BigDecimal amount,
                               RefundType refundType,
                               String reason) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        BigDecimal paymentAmount = payment.getAmount();

        if (refundType == RefundType.FULL) {
            amount = paymentAmount;
        } else {
            // Partial refund validation
            if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0 ||
                amount.compareTo(paymentAmount) > 0) {
                throw new IllegalArgumentException("Invalid partial refund amount");
            }
        }

        Refund refund = new Refund(payment, amount, refundType);
        refund.setReason(reason);
        refund.setStatus(RefundStatus.PENDING);

        // createdAt, updatedAt sẽ tự set bằng @PrePersist

        return refundRepository.save(refund);
    }

    /**
     * Process a refund (simulate payment gateway)
     */
    public Refund processRefund(Long refundId, boolean success) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("Refund not found"));

        if (success) {
            refund.setStatus(RefundStatus.SUCCESS);
            refund.setRefundedAt(LocalDateTime.now());
            refund.setRefundTransactionId(generateRefundTransactionId());

            // If full refund -> mark order as refunded
            if (refund.getRefundType() == RefundType.FULL) {
                Order order = refund.getPayment().getOrder();
                order.setStatus(Order.OrderStatus.REFUNDED);
                orderRepository.update(order);
            }

        } else {
            refund.setStatus(RefundStatus.FAILED);
        }

        refund.setUpdatedAt(LocalDateTime.now());
        refundRepository.update(refund);
        return refund;
    }

    public Optional<Refund> getRefundById(Long refundId) {
        return refundRepository.findById(refundId);
    }

    public List<Refund> getRefundsByPaymentId(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId);
    }

    public List<Refund> getRefundsByStatus(RefundStatus status) {
        return refundRepository.findByStatus(status.name());
    }

    /**
     * Calculate total refunded amount for a payment
     */
    public BigDecimal calculateTotalRefunded(Long paymentId) {

        List<Refund> refunds = refundRepository.findByPaymentId(paymentId);

        return refunds.stream()
                .filter(r -> r.getStatus() == RefundStatus.SUCCESS)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Generate a unique refund transaction ID
     */
    private String generateRefundTransactionId() {
        return "REFUND-" + System.currentTimeMillis() + "-" + (int)(ThreadLocalRandom.current().nextDouble() * 10000);
    }
}
