package com.ucop.dto;

import java.math.BigDecimal;

public class PromotionApplyResultDTO {
    private boolean success;
    private String message;
    private String promotionCode;
    private BigDecimal discountAmount;
    private BigDecimal originalTotal;
    private BigDecimal finalTotal;
    private String discountType;

    // Constructors
    public PromotionApplyResultDTO() {}

    public PromotionApplyResultDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static PromotionApplyResultDTO success(String promotionCode, BigDecimal discountAmount, 
                                                   BigDecimal originalTotal, BigDecimal finalTotal, 
                                                   String discountType) {
        PromotionApplyResultDTO result = new PromotionApplyResultDTO();
        result.setSuccess(true);
        result.setMessage("Áp dụng mã giảm giá thành công!");
        result.setPromotionCode(promotionCode);
        result.setDiscountAmount(discountAmount);
        result.setOriginalTotal(originalTotal);
        result.setFinalTotal(finalTotal);
        result.setDiscountType(discountType);
        return result;
    }

    public static PromotionApplyResultDTO failure(String message) {
        return new PromotionApplyResultDTO(false, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getOriginalTotal() {
        return originalTotal;
    }

    public void setOriginalTotal(BigDecimal originalTotal) {
        this.originalTotal = originalTotal;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
}
