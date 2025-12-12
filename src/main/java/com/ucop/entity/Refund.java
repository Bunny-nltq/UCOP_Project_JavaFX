package com.ucop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "refunds")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type")
    private RefundType refundType;
    
    @Enumerated(EnumType.STRING)
    private RefundStatus status = RefundStatus.PENDING;
    private String reason;
    private String refundTransactionId;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String createdBy;
    private String updatedBy;
    private LocalDateTime refundedAt;

    public Refund() {}

    public Refund(Payment payment, BigDecimal amount, RefundType refundType) {
        this.payment = payment;
        this.amount = amount;
        this.refundType = refundType;
        this.status = RefundStatus.PENDING;
    }

    // Auto timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public String getRefundTransactionId() { return refundTransactionId; }

    public void setRefundTransactionId(String refundTransactionId) { this.refundTransactionId = refundTransactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }

    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getRefundedAt() { return refundedAt; }

    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }

    // ENUMS
    public enum RefundType {
        FULL,
        PARTIAL
    }

    public enum RefundStatus {
        PENDING,
        PROCESSING,
        SUCCESS,
        FAILED,
        CANCELED
    }
}
