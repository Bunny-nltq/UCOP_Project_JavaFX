package com.ucop.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.ucop.entity.Product;
import com.ucop.util.HibernateUtil;

/**
 * DAO cho bảng products
 * Truy vấn trực tiếp từ bảng products trong database
 */
public class ProductDAO {
    
    private final SessionFactory sessionFactory;
    
    public ProductDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    public ProductDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Lưu hoặc cập nhật product
     */
    public Product save(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(product);
            transaction.commit();
            return product;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error saving product", e);
        }
    }
    
    /**
     * Tìm product theo ID
     */
    public Optional<Product> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Product product = session.get(Product.class, id);
            return Optional.ofNullable(product);
        } catch (Exception e) {
            throw new RuntimeException("Error finding product by id: " + id, e);
        }
    }
    
    /**
     * Lấy tất cả products
     */
    public List<Product> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery("FROM Product", Product.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all products", e);
        }
    }
    
    /**
     * Lấy các products đang active
     */
    public List<Product> findActiveProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE isActive = true ORDER BY createdAt DESC", 
                Product.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding active products", e);
        }
    }
    
    /**
     * Lấy products có trong kho (stock > 0)
     */
    public List<Product> findInStockProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE isActive = true AND stockQuantity > 0 ORDER BY createdAt DESC", 
                Product.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding in-stock products", e);
        }
    }
    
    /**
     * Tìm products theo category
     */
    public List<Product> findByCategory(String category) {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE category = :category AND isActive = true", 
                Product.class);
            query.setParameter("category", category);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding products by category: " + category, e);
        }
    }
    
    /**
     * Tìm products theo tên (chứa keyword)
     */
    public List<Product> findByNameContaining(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE LOWER(name) LIKE LOWER(:keyword) AND isActive = true", 
                Product.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding products by keyword: " + keyword, e);
        }
    }
    
    /**
     * Tìm products theo khoảng giá
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE price BETWEEN :minPrice AND :maxPrice AND isActive = true", 
                Product.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding products by price range", e);
        }
    }
    
    /**
     * Lấy danh sách categories duy nhất
     */
    public List<String> findAllCategories() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery(
                "SELECT DISTINCT category FROM Product WHERE category IS NOT NULL ORDER BY category", 
                String.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding categories", e);
        }
    }
    
    /**
     * Đếm tổng số products
     */
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Product p", 
                Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error counting products", e);
        }
    }
    
    /**
     * Đếm số products active
     */
    public long countActive() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.isActive = true", 
                Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error counting active products", e);
        }
    }
    
    /**
     * Cập nhật stock quantity
     */
    public void updateStock(Long productId, Long quantity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, productId);
            if (product != null) {
                product.setStockQuantity(quantity);
                session.update(product);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error updating stock for product id: " + productId, e);
        }
    }
    
    /**
     * Xóa product
     */
    public void delete(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(product);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting product", e);
        }
    }
    
    /**
     * Xóa product theo ID
     */
    public void deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting product by id: " + id, e);
        }
    }
}
