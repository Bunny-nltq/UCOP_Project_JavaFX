package com.ucop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refunds")
public class Refund extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundType refundType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status = RefundStatus.PENDING;

    @Column
    private String reason;

    @Column
    private LocalDateTime refundedAt;

    @Column(unique = true)
    private String refundTransactionId;

    public Refund() {}

    public Refund(Payment payment, BigDecimal amount, RefundType refundType) {
        this.payment = payment;
        this.amount = amount;
        this.refundType = refundType;
        this.status = RefundStatus.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refund refund = (Refund) o;
        return Objects.equals(id, refund.id) && Objects.equals(refundTransactionId, refund.refundTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, refundTransactionId);
    }

    // Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Payment getPayment() { return payment; }

    public void setPayment(Payment payment) { this.payment = payment; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public RefundType getRefundType() { return refundType; }

    public void setRefundType(RefundType refundType) { this.refundType = refundType; }

    public RefundStatus getStatus() { return status; }

    public void setStatus(RefundStatus status) { this.status = status; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getRefundedAt() { return refundedAt; }

    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }

    public String getRefundTransactionId() { return refundTransactionId; }

    public void setRefundTransactionId(String refundTransactionId) { this.refundTransactionId = refundTransactionId; }
}
