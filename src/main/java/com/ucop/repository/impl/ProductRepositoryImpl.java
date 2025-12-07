package com.ucop.repository.impl;

import com.ucop.entity.Product;
import com.ucop.repository.ProductRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {
    
    private final SessionFactory sessionFactory;
    
    public ProductRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Product save(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(product);
            transaction.commit();
            return product;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Product product = session.get(Product.class, id);
            return Optional.ofNullable(product);
        }
    }
    
    @Override
    public List<Product> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Product", Product.class).list();
        }
    }
    
    @Override
    public List<Product> findByCategory(String category) {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE category = :category", Product.class);
            query.setParameter("category", category);
            return query.list();
        }
    }
    
    @Override
    public List<Product> findByNameContaining(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE LOWER(name) LIKE LOWER(:keyword)", Product.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.list();
        }
    }
    
    @Override
    public List<Product> findActiveProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE isActive = true", Product.class);
            return query.list();
        }
    }
    
    @Override
    public List<Product> findInStockProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE isActive = true AND stockQuantity > 0", Product.class);
            return query.list();
        }
    }
    
    @Override
    public void delete(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(product);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
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
            throw e;
        }
    }
    
    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
            return query.uniqueResult();
        }
    }
}
