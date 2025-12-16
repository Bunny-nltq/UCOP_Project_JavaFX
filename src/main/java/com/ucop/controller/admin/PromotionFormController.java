package com.ucop.controller.admin;

import com.ucop.entity.Promotion;
import com.ucop.service.PromotionService;

import javafx.fxml.FXML;
// ... các import khác

public class PromotionFormController {
    
    // Các field có sẵn...
    @FXML
    private javafx.scene.control.TextField promotionNameField;
    // ...
    
    // Thêm các field mới (nếu chưa có)
    private DashboardController dashboardController;
    private PromotionService promotionService;
    private Promotion promotion;
    
    // Các method có sẵn...
    @FXML
    public void initialize() {
        // ...
    }
    
    // THÊM CÁC METHOD MỚI VÀO ĐÂY (bên trong class)
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
    
    // Các method khác...
}