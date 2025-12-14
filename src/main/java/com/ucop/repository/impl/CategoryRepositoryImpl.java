package com.ucop.repository.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.ucop.entity.Category;
import com.ucop.repository.CategoryRepository;

public class CategoryRepositoryImpl implements CategoryRepository {
    
    private final SessionFactory sessionFactory;
    
    public CategoryRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Category save(Category category) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(category);
            transaction.commit();
            return category;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Category> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Category category = session.get(Category.class, id);
            return Optional.ofNullable(category);
        }
    }
    
    @Override
    public List<Category> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Category", Category.class).list();
        }
    }
    
    @Override
    public List<Category> findActiveCategories() {
        try (Session session = sessionFactory.openSession()) {
            Query<Category> query = session.createQuery(
                "FROM Category WHERE status = 1", Category.class);
            return query.list();
        }
    }
    
    @Override
    public void delete(Category category) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(category);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Category category = session.get(Category.class, id);
            if (category != null) {
                session.delete(category);
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
            return session.createQuery("SELECT COUNT(*) FROM Category", Long.class).uniqueResult();
        }
    }
}
