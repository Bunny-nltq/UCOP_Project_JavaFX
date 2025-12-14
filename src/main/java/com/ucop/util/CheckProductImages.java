package com.ucop.util;

import org.hibernate.Session;
import java.util.List;

/**
 * Utility để kiểm tra hình ảnh sản phẩm trong database
 */
public class CheckProductImages {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  KIỂM TRA HÌNH ẢNH SẢN PHẨM");
        System.out.println("========================================");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Đếm sản phẩm
            Long totalProducts = (Long) session.createQuery("SELECT COUNT(i) FROM Item i").uniqueResult();
            System.out.println("\n📊 THỐNG KÊ:");
            System.out.println("✓ Tổng số sản phẩm: " + totalProducts);
            
            // Đếm sản phẩm có ảnh
            Long productsWithImages = (Long) session.createQuery(
                "SELECT COUNT(i) FROM Item i WHERE i.imageUrl IS NOT NULL AND i.imageUrl != ''"
            ).uniqueResult();
            System.out.println("✓ Sản phẩm có URL hình ảnh: " + productsWithImages);
            System.out.println("✓ Sản phẩm chưa có ảnh: " + (totalProducts - productsWithImages));
            
            // Hiển thị mẫu sản phẩm
            displaySampleProducts(session);
            
            if (productsWithImages == 0) {
                System.out.println("\n⚠️  CẢNH BÁO: Chưa có sản phẩm nào có hình ảnh!");
                System.out.println("\n💡 HƯỚNG DẪN CẬP NHẬT:");
                System.out.println("1. Chạy SQL script trong database/update_product_images.sql");
                System.out.println("   HOẶC");
                System.out.println("2. Chạy class UpdateProductImages để tự động cập nhật hình ảnh");
            } else {
                System.out.println("\n✅ Hình ảnh sản phẩm đã được cấu hình!");
                System.out.println("🚀 Ứng dụng sẽ hiển thị hình ảnh từ database.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displaySampleProducts(Session session) {
        System.out.println("\n📋 MẪU SẢN PHẨM (10 sản phẩm đầu):");
        System.out.println("─".repeat(90));
        System.out.printf("%-5s | %-35s | %-45s%n", "ID", "TÊN SẢN PHẨM", "URL HÌNH ẢNH");
        System.out.println("─".repeat(90));
        
        @SuppressWarnings("unchecked")
        List<Object[]> products = session.createQuery(
            "SELECT i.id, i.name, i.imageUrl FROM Item i ORDER BY i.id"
        ).setMaxResults(10).list();
        
        for (Object[] row : products) {
            Long id = (Long) row[0];
            String name = (String) row[1];
            String imageUrl = (String) row[2];
            
            String displayName = name.length() > 35 ? name.substring(0, 32) + "..." : name;
            String displayUrl = imageUrl != null 
                ? (imageUrl.length() > 45 ? imageUrl.substring(0, 42) + "..." : imageUrl)
                : "[Chưa có ảnh]";
            
            System.out.printf("%-5d | %-35s | %-45s%n", id, displayName, displayUrl);
        }
        
        System.out.println("─".repeat(90));
    }
}
