package com.ucop.repository.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.ucop.entity.Item;
import com.ucop.repository.ProductRepository;

public class ProductRepositoryImpl implements ProductRepository {
    
    private final SessionFactory sessionFactory;
    
    public ProductRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Item save(Item item) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(item);
            transaction.commit();
            return item;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Item> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Item item = session.get(Item.class, id);
            return Optional.ofNullable(item);
        }
    }
    
    @Override
    public List<Item> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Item", Item.class).list();
        }
    }
    
    @Override
    public List<Item> findByCategory(String category) {
        try (Session session = sessionFactory.openSession()) {
            Query<Item> query = session.createQuery(
                "FROM Item i WHERE i.category.name = :category", Item.class);
            query.setParameter("category", category);
            return query.list();
        }
    }
    
    @Override
    public List<Item> findByNameContaining(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            Query<Item> query = session.createQuery(
                "FROM Item WHERE LOWER(name) LIKE LOWER(:keyword)", Item.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.list();
        }
    }
    
    @Override
    public List<Item> findActiveProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Item> query = session.createQuery(
                "FROM Item WHERE status = 1", Item.class);
            return query.list();
        }
    }
    
    @Override
    public List<Item> findInStockProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Item> query = session.createQuery(
                "FROM Item WHERE status = 1 AND stock > 0", Item.class);
            return query.list();
        }
    }
    
    @Override
    public void delete(Item item) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(item);
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
            Item product = session.get(Item.class, id);
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
            Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Item p", Long.class);
            return query.uniqueResult();
        }
    }
}
