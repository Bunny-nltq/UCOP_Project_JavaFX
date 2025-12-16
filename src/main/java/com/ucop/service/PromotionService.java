package com.ucop.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ucop.dao.PromotionApplyResultDAO;
import com.ucop.dao.PromotionDAO;
import com.ucop.entity.Order;
import com.ucop.entity.Promotion;
import com.ucop.entity.PromotionUsage;
import com.ucop.repository.PromotionRepository;
import com.ucop.repository.PromotionUsageRepository;

public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;

    public PromotionService(PromotionRepository promotionRepository, 
                           PromotionUsageRepository promotionUsageRepository) {
        this.promotionRepository = promotionRepository;
        this.promotionUsageRepository = promotionUsageRepository;
    }

    // CRUD Operations
    public Promotion createPromotion(PromotionDAO dto) {
        validatePromotionDTO(dto);
        
        Promotion promotion = new Promotion();
        mapDtoToEntity(dto, promotion);
        
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, PromotionDAO dto) {
        Promotion promotion = promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
        
        validatePromotionDTO(dto);
        mapDtoToEntity(dto, promotion);
        
        return promotionRepository.save(promotion);
    }

    public void deletePromotion(Long id) {
        promotionRepository.delete(id);
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
    }

    public Promotion getPromotionByCode(String code) {
        return promotionRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Promotion not found with code: " + code));
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findActivePromotions();
    }

    // Apply Promotion Logic
    public PromotionApplyResultDAO applyPromotion(String promotionCode, Order order, Long accountId) {
        // Find promotion
        Promotion promotion = promotionRepository.findByCode(promotionCode)
            .orElse(null);
        
        if (promotion == null) {
            return PromotionApplyResultDAO.failure("Mã giảm giá không tồn tại!");
        }

        // Validate promotion
        String validationError = validatePromotionForOrder(promotion, order, accountId);
        if (validationError != null) {
            return PromotionApplyResultDAO.failure(validationError);
        }

        // Calculate discount
        BigDecimal discountAmount = calculateDiscount(promotion, order);
        BigDecimal originalTotal = order.getGrandTotal();
        BigDecimal finalTotal = originalTotal.subtract(discountAmount);

        // Ensure final total is not negative
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        // Create promotion usage record
        PromotionUsage usage = new PromotionUsage(promotion, order, accountId, discountAmount);
        promotionUsageRepository.save(usage);

        // Update promotion usage count
        promotionRepository.updateUsageCount(promotion.getId());

        // Update order
        if ("CART".equals(promotion.getDiscountType())) {
            order.setCartDiscount(discountAmount);
        } else {
            order.setItemDiscount(discountAmount);
        }
        order.setPromotionCode(promotionCode);
        order.setGrandTotal(finalTotal);
        order.setAmountDue(finalTotal);

        return PromotionApplyResultDAO.success(
            promotionCode, 
            discountAmount, 
            originalTotal, 
            finalTotal, 
            promotion.getDiscountType()
        );
    }

    private String validatePromotionForOrder(Promotion promotion, Order order, Long accountId) {
        LocalDateTime now = LocalDateTime.now();

        // Check if active
        if (!promotion.getActive()) {
            return "Mã giảm giá đã bị vô hiệu hóa!";
        }

        // Check date range
        if (now.isBefore(promotion.getStartDate())) {
            return "Mã giảm giá chưa có hiệu lực!";
        }
        if (now.isAfter(promotion.getEndDate())) {
            return "Mã giảm giá đã hết hạn!";
        }

        // Check max usage total
        if (promotion.getMaxUsageTotal() != null && 
            promotion.getUsageCount() >= promotion.getMaxUsageTotal()) {
            return "Mã giảm giá đã hết lượt sử dụng!";
        }

        // Check max usage per user
        long userUsageCount = promotionUsageRepository.countByPromotionAndAccount(
        promotion.getId(), accountId);
        if (promotion.getMaxUsagePerUser() != null && 
            userUsageCount >= promotion.getMaxUsagePerUser()) {
            return "Bạn đã sử dụng hết số lần áp dụng mã này!";
        }

        // Check minimum order amount
        if (promotion.getMinOrderAmount() != null && 
            order.getSubtotal().compareTo(promotion.getMinOrderAmount()) < 0) {
            return String.format("Đơn hàng tối thiểu phải từ %,.0f VNĐ!", 
                promotion.getMinOrderAmount());
        }

        // Check applicable items
        if ("SPECIFIC_ITEMS".equals(promotion.getApplicableTo())) {
            List<Long> applicableItemIds = parseItemIds(promotion.getApplicableItemIds());
            boolean hasApplicableItem = order.getItems().stream()
                .anyMatch(item -> applicableItemIds.contains(item.getItemId()));
            
            if (!hasApplicableItem) {
                return "Mã giảm giá không áp dụng cho sản phẩm trong giỏ hàng!";
            }
        }

        return null;
    }

    private BigDecimal calculateDiscount(Promotion promotion, Order order) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (promotion.getDiscountType()) {
            case "PERCENTAGE":
                // Calculate percentage discount
                discount = order.getSubtotal()
                    .multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                
                // Apply max discount limit
                if (promotion.getMaxDiscountAmount() != null && 
                    discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                    discount = promotion.getMaxDiscountAmount();
                }
                break;

            case "FIXED_AMOUNT":
                discount = promotion.getDiscountValue();
                break;

            case "ITEM":
                // Discount for specific items
                if ("SPECIFIC_ITEMS".equals(promotion.getApplicableTo())) {
                    List<Long> applicableItemIds = parseItemIds(promotion.getApplicableItemIds());
                    discount = order.getItems().stream()
                        .filter(item -> applicableItemIds.contains(item.getItemId()))
                        .map(item -> item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
                            .multiply(promotion.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                break;

            case "CART":
                discount = promotion.getDiscountValue();
                break;
        }

        // Ensure discount doesn't exceed order total
        if (discount.compareTo(order.getSubtotal()) > 0) {
            discount = order.getSubtotal();
        }

        return discount;
    }

    private List<Long> parseItemIds(String itemIdsStr) {
        if (itemIdsStr == null || itemIdsStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(itemIdsStr.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }

    private void validatePromotionDTO(PromotionDAO dto) {
        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion code is required");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name is required");
        }
        if (dto.getDiscountType() == null) {
            throw new IllegalArgumentException("Discount type is required");
        }
        if (dto.getDiscountValue() == null || dto.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than 0");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    private void mapDtoToEntity(PromotionDAO dto, Promotion promotion) {
        promotion.setCode(dto.getCode());
        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setDiscountType(dto.getDiscountType());
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setMinOrderAmount(dto.getMinOrderAmount());
        promotion.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        promotion.setApplicableTo(dto.getApplicableTo() != null ? dto.getApplicableTo() : "ALL");
        promotion.setApplicableItemIds(dto.getApplicableItemIds());
        promotion.setMaxUsageTotal(dto.getMaxUsageTotal());
        promotion.setMaxUsagePerUser(dto.getMaxUsagePerUser() != null ? dto.getMaxUsagePerUser() : 1);
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.getActive() != null ? dto.getActive() : true);
        promotion.setStackable(dto.getStackable() != null ? dto.getStackable() : false);
    }

    public PromotionDAO convertToDTO(Promotion promotion) {
        PromotionDAO dto = new PromotionDAO();
        dto.setId(promotion.getId());
        dto.setCode(promotion.getCode());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setMinOrderAmount(promotion.getMinOrderAmount());
        dto.setMaxDiscountAmount(promotion.getMaxDiscountAmount());
        dto.setApplicableTo(promotion.getApplicableTo());
        dto.setApplicableItemIds(promotion.getApplicableItemIds());
        dto.setMaxUsageTotal(promotion.getMaxUsageTotal());
        dto.setMaxUsagePerUser(promotion.getMaxUsagePerUser());
        dto.setUsageCount(promotion.getUsageCount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setActive(promotion.getActive());
        dto.setStackable(promotion.getStackable());
        return dto;
    }

    /**
     * Apply promotion for cart (without order)
     * Used in customer UI to preview discount
     */
    public PromotionApplyResultDAO applyPromotion(String promotionCode, Long accountId, 
                                                   BigDecimal cartTotal, List<Long> itemIds) {
        // Find promotion by code
        Promotion promotion = promotionRepository.findByCode(promotionCode)
            .orElse(null);

        if (promotion == null) {
            return PromotionApplyResultDAO.failure("Mã giảm giá không tồn tại!");
        }

        // Validate promotion
        LocalDateTime now = LocalDateTime.now();

        if (!promotion.getActive()) {
            return PromotionApplyResultDAO.failure("Mã giảm giá đã bị vô hiệu hóa!");
        }

        if (now.isBefore(promotion.getStartDate())) {
            return PromotionApplyResultDAO.failure("Mã giảm giá chưa có hiệu lực!");
        }

        if (now.isAfter(promotion.getEndDate())) {
            return PromotionApplyResultDAO.failure("Mã giảm giá đã hết hạn!");
        }

        if (promotion.getMaxUsageTotal() != null && 
            promotion.getUsageCount() >= promotion.getMaxUsageTotal()) {
            return PromotionApplyResultDAO.failure("Mã giảm giá đã hết lượt sử dụng!");
        }

        if (accountId != null && promotion.getMaxUsagePerUser() != null) {
        long userUsageCount = promotionUsageRepository.countByPromotionAndAccount(
            promotion.getId(), accountId);
            if (userUsageCount >= promotion.getMaxUsagePerUser()) {
                return PromotionApplyResultDAO.failure("Bạn đã sử dụng hết số lần áp dụng mã này!");
            }
        }

        if (promotion.getMinOrderAmount() != null && 
            cartTotal.compareTo(promotion.getMinOrderAmount()) < 0) {
            return PromotionApplyResultDAO.failure(
                String.format("Đơn hàng tối thiểu phải từ %,.0f VNĐ!", promotion.getMinOrderAmount())
            );
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (promotion.getDiscountType()) {
            case "PERCENTAGE":
                discount = cartTotal
                    .multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                
                if (promotion.getMaxDiscountAmount() != null && 
                    discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                    discount = promotion.getMaxDiscountAmount();
                }
                break;

            case "FIXED_AMOUNT":
            case "CART":
                discount = promotion.getDiscountValue();
                break;
        }

        if (discount.compareTo(cartTotal) > 0) {
            discount = cartTotal;
        }

        BigDecimal finalTotal = cartTotal.subtract(discount);

        return PromotionApplyResultDAO.success(
            promotionCode, 
            discount, 
            cartTotal, 
            finalTotal, 
            promotion.getDiscountType()
        );
    }
}
