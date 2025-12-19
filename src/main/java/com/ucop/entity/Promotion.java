package com.ucop.entity;



import jakarta.persistence.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;



@Entity

@Table(name = "promotions")

public class Promotion {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @Column(name = "code", unique = true, nullable = false, length = 50)

    private String code;



    @Column(name = "name", nullable = false, length = 100)

    private String name;



    @Column(name = "description", length = 500)

    private String description;



    @Column(name = "discount_type", nullable = false, length = 20)

    private String discountType; // PERCENTAGE, FIXED_AMOUNT, ITEM, CART



    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)

    private BigDecimal discountValue;



    @Column(name = "min_order_amount", precision = 19, scale = 4)

    private BigDecimal minOrderAmount;



    @Column(name = "max_discount_amount", precision = 19, scale = 4)

    private BigDecimal maxDiscountAmount;



    @Column(name = "applicable_to", nullable = false, length = 20)

    private String applicableTo = "ALL"; // ALL, SPECIFIC_ITEMS, SPECIFIC_CATEGORIES



    @Column(name = "applicable_item_ids", columnDefinition = "TEXT")

    private String applicableItemIds; // Comma-separated item IDs



    @Column(name = "max_usage_total")

    private Integer maxUsageTotal;



    @Column(name = "max_usage_per_user")

    private Integer maxUsagePerUser = 1;



    @Column(name = "usage_count")

    private Integer usageCount = 0;



    @Column(name = "start_date", nullable = false)

    private LocalDateTime startDate;



    @Column(name = "end_date", nullable = false)

    private LocalDateTime endDate;



    @Column(name = "is_active", nullable = false)

    private Boolean isActive = true;



    @Column(name = "is_stackable", nullable = false)

    private Boolean isStackable = false;



    @Column(name = "created_at", nullable = false, updatable = false)

    private LocalDateTime createdAt;



    @Column(name = "updated_at", nullable = false)

    private LocalDateTime updatedAt;



    @Column(name = "created_by", length = 100)

    private String createdBy;



    @Column(name = "updated_by", length = 100)

    private String updatedBy;



    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    private List<PromotionUsage> usages = new ArrayList<>();



    // Constructors

    public Promotion() {

        this.createdAt = LocalDateTime.now();

        this.updatedAt = LocalDateTime.now();

    }



    @PreUpdate

    public void preUpdate() {

        this.updatedAt = LocalDateTime.now();

    }



    // Getters and Setters

    public Long getId() {

        return id;

    }



    public void setId(Long id) {

        this.id = id;

    }



    public String getCode() {

        return code;

    }



    public void setCode(String code) {

        this.code = code;

    }



    public String getName() {

        return name;

    }



    public void setName(String name) {

        this.name = name;

    }



    public String getDescription() {

        return description;

    }



    public void setDescription(String description) {

        this.description = description;

    }



    public String getDiscountType() {

        return discountType;

    }



    public void setDiscountType(String discountType) {

        this.discountType = discountType;

    }



    public BigDecimal getDiscountValue() {

        return discountValue;

    }



    public void setDiscountValue(BigDecimal discountValue) {

        this.discountValue = discountValue;

    }



    public BigDecimal getMinOrderAmount() {

        return minOrderAmount;

    }



    public void setMinOrderAmount(BigDecimal minOrderAmount) {

        this.minOrderAmount = minOrderAmount;

    }



    public BigDecimal getMaxDiscountAmount() {

        return maxDiscountAmount;

    }



    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {

        this.maxDiscountAmount = maxDiscountAmount;

    }



    public String getApplicableTo() {

        return applicableTo;

    }



    public void setApplicableTo(String applicableTo) {

        this.applicableTo = applicableTo;

    }



    public String getApplicableItemIds() {

        return applicableItemIds;

    }



    public void setApplicableItemIds(String applicableItemIds) {

        this.applicableItemIds = applicableItemIds;

    }



    public Integer getMaxUsageTotal() {

        return maxUsageTotal;

    }



    public void setMaxUsageTotal(Integer maxUsageTotal) {

        this.maxUsageTotal = maxUsageTotal;

    }



    public Integer getMaxUsagePerUser() {

        return maxUsagePerUser;

    }



    public void setMaxUsagePerUser(Integer maxUsagePerUser) {

        this.maxUsagePerUser = maxUsagePerUser;

    }



    public Integer getUsageCount() {

        return usageCount;

    }



    public void setUsageCount(Integer usageCount) {

        this.usageCount = usageCount;

    }



    public LocalDateTime getStartDate() {

        return startDate;

    }



    public void setStartDate(LocalDateTime startDate) {

        this.startDate = startDate;

    }



    public LocalDateTime getEndDate() {

        return endDate;

    }



    public void setEndDate(LocalDateTime endDate) {

        this.endDate = endDate;

    }



    public Boolean getActive() {

        return isActive;

    }



    public void setActive(Boolean active) {

        isActive = active;

    }



    public Boolean getStackable() {

        return isStackable;

    }



    public void setStackable(Boolean stackable) {

        isStackable = stackable;

    }



    public LocalDateTime getCreatedAt() {

        return createdAt;

    }



    public void setCreatedAt(LocalDateTime createdAt) {

        this.createdAt = createdAt;

    }



    public LocalDateTime getUpdatedAt() {

        return updatedAt;

    }



    public void setUpdatedAt(LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;

    }



    public String getCreatedBy() {

        return createdBy;

    }



    public void setCreatedBy(String createdBy) {

        this.createdBy = createdBy;

    }



    public String getUpdatedBy() {

        return updatedBy;

    }



    public void setUpdatedBy(String updatedBy) {

        this.updatedBy = updatedBy;

    }



    public List<PromotionUsage> getUsages() {

        return usages;

    }



    public void setUsages(List<PromotionUsage> usages) {

        this.usages = usages;

    }



    // Business Methods

    public boolean isValid() {

        LocalDateTime now = LocalDateTime.now();

        return isActive && 

               startDate.isBefore(now) && 

               endDate.isAfter(now) &&

               (maxUsageTotal == null || usageCount < maxUsageTotal);

    }



    public void incrementUsageCount() {

        this.usageCount++;

    }

}




