package com.ucop.repository.impl;

import com.ucop.entity.Cart;
import com.ucop.repository.CartRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class CartRepositoryImpl implements CartRepository {
    
    private final SessionFactory sessionFactory;
    
    public CartRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Cart save(Cart cart) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(cart);
            transaction.commit();
            return cart;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Cart> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Cart cart = session.createQuery(
                "FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :id", Cart.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(cart);
        }
    }
    
    @Override
    public Optional<Cart> findByAccountId(Long accountId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Cart> query = session.createQuery(
                "FROM Cart c LEFT JOIN FETCH c.items WHERE c.accountId = :accountId", Cart.class);
            query.setParameter("accountId", accountId);
            return query.uniqueResultOptional();
        }
    }
    
    @Override
    public List<Cart> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Cart", Cart.class).list();
        }
    }
    
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Cart cart = session.get(Cart.class, id);
            if (cart != null) {
                session.delete(cart);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public void update(Cart cart) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(cart);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
