package com.ucop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // payments.order_id (NOT NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // DB: payment_method varchar(50) NULL
    // => để nullable = true
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    // DB: amount decimal(19,4) NULL
    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;

    // DB: status varchar(50) NULL
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PaymentStatus status;

    // DB: transaction_id varchar(100) NULL (không thấy unique trong schema bạn gửi)
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // DB: reference_number varchar(100) NULL
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    // DB: notes varchar(500) NULL
    @Column(name = "notes", length = 500)
    private String notes;

    // DB: created_at datetime NULL default current_timestamp()
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // DB: updated_at datetime NULL default current_timestamp() on update current_timestamp()
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // DB: created_by varchar(100) NULL
    @Column(name = "created_by", length = 100)
    private String createdBy;

    // DB: updated_by varchar(100) NULL
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // DB: paid_at datetime(6) NULL
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Payment() {}

    public Payment(Order order, PaymentMethod method, BigDecimal amount) {
        this.order = order;
        this.paymentMethod = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        // DB cho phép NULL nhưng set cho chắc
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = PaymentStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ===== Getters / Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    // ===== ENUMS =====
    public enum PaymentMethod {
        COD, BANK_TRANSFER, GATEWAY, WALLET
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, SUCCESS, FAILED, CANCELED
    }
}
