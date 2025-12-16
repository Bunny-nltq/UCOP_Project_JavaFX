package com.ucop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotion_usages")
public class PromotionUsage extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private LocalDateTime usedAt = LocalDateTime.now();

    public PromotionUsage() {}

    public PromotionUsage(Promotion promotion, Order order, Long accountId, BigDecimal discountAmount) {
        this.promotion = promotion;
        this.order = order;
        this.accountId = accountId;
        this.discountAmount = discountAmount;
        this.usedAt = LocalDateTime.now();
    }

    // Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Promotion getPromotion() { return promotion; }

    public void setPromotion(Promotion promotion) { this.promotion = promotion; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    public Long getAccountId() { return accountId; }

    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public BigDecimal getDiscountAmount() { return discountAmount; }

    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public LocalDateTime getUsedAt() { return usedAt; }

    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
}
