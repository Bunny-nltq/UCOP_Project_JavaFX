package com.ucop.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Utility class to update product images in database
 */
public class UpdateProductImages {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  CẬP NHẬT HÌNH ẢNH SẢN PHẨM");
        System.out.println("========================================");
        
        Transaction transaction = null;
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            System.out.println("\n📊 Kiểm tra sản phẩm hiện tại...");
            
            // Đếm sản phẩm
            Long totalProducts = (Long) session.createQuery("SELECT COUNT(i) FROM Item i").uniqueResult();
            System.out.println("✓ Tổng số sản phẩm: " + totalProducts);
            
            // Đếm sản phẩm có ảnh
            Long productsWithImages = (Long) session.createQuery(
                "SELECT COUNT(i) FROM Item i WHERE i.imageUrl IS NOT NULL AND i.imageUrl != ''"
            ).uniqueResult();
            System.out.println("✓ Sản phẩm có URL hình ảnh: " + productsWithImages);
            System.out.println("✓ Sản phẩm chưa có ảnh: " + (totalProducts - productsWithImages));
            
            System.out.println("\n🔄 Đang cập nhật hình ảnh...");
            
            // Cập nhật hình ảnh bằng placeholder đẹp
            int updated = session.createQuery(
                "UPDATE Item i SET i.imageUrl = CONCAT('https://dummyimage.com/400x400/4CAF50/ffffff&text=', REPLACE(SUBSTRING(i.name, 1, 20), ' ', '+')) " +
                "WHERE i.imageUrl IS NULL OR i.imageUrl = ''"
            ).executeUpdate();
            
            System.out.println("✓ Đã cập nhật " + updated + " sản phẩm");
            
            // Cập nhật hình ảnh cụ thể cho một số loại sản phẩm
            updateSpecificCategories(session);
            
            transaction.commit();
            
            System.out.println("\n✅ HOÀN THÀNH!");
            System.out.println("📝 Tất cả sản phẩm đã có hình ảnh.");
            System.out.println("🚀 Khởi động lại ứng dụng để xem thay đổi.\n");
            
            // Hiển thị mẫu
            displaySampleProducts(session);
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void updateSpecificCategories(Session session) {
        System.out.println("\n🎨 Cập nhật hình ảnh theo danh mục...");
        
        // Văn phòng phẩm
        updateByPattern(session, "%Bút%", "https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?w=400");
        updateByPattern(session, "%Kéo%", "https://images.unsplash.com/photo-1554475900-4b0c68e7db12?w=400");
        updateByPattern(session, "%Thước%", "https://images.unsplash.com/photo-1544816155-12df9643f363?w=400");
        
        // Điện tử
        updateByPattern(session, "%Máy Tính%", "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400");
        updateByPattern(session, "%Tai Nghe%", "https://images.unsplash.com/photo-1545127398-14699f92334b?w=400");
        updateByPattern(session, "%Chuột%", "https://images.unsplash.com/photo-1527814050087-3793815479db?w=400");
        
        System.out.println("✓ Đã cập nhật hình ảnh theo danh mục");
    }
    
    private static void updateByPattern(Session session, String namePattern, String imageUrl) {
        try {
            int count = session.createQuery(
                "UPDATE Item i SET i.imageUrl = :url WHERE i.name LIKE :pattern"
            )
            .setParameter("url", imageUrl)
            .setParameter("pattern", namePattern)
            .executeUpdate();
            
            if (count > 0) {
                System.out.println("  - Cập nhật " + count + " sản phẩm: " + namePattern);
            }
        } catch (Exception e) {
            System.err.println("  ⚠ Lỗi cập nhật " + namePattern + ": " + e.getMessage());
        }
    }
    
    private static void displaySampleProducts(Session session) {
        System.out.println("\n📋 MẪU SẢN PHẨM (10 sản phẩm đầu):");
        System.out.println("─".repeat(80));
        
        @SuppressWarnings("unchecked")
        List<Object[]> products = session.createQuery(
            "SELECT i.id, i.name, i.imageUrl FROM Item i ORDER BY i.id"
        ).setMaxResults(10).list();
        
        for (Object[] row : products) {
            Long id = (Long) row[0];
            String name = (String) row[1];
            String imageUrl = (String) row[2];
            
            String displayUrl = imageUrl != null && imageUrl.length() > 50 
                ? imageUrl.substring(0, 47) + "..." 
                : imageUrl;
            
            System.out.printf("ID: %-5d | %-30s | %s%n", id, 
                name.length() > 30 ? name.substring(0, 27) + "..." : name,
                displayUrl != null ? displayUrl : "[Chưa có ảnh]");
        }
        
        System.out.println("─".repeat(80));
    }
}
