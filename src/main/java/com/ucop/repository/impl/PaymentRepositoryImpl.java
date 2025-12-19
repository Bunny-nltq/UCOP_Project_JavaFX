package com.ucop.repository.impl;

import com.ucop.entity.Payment;
import com.ucop.repository.PaymentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class PaymentRepositoryImpl implements PaymentRepository {
    
    private final SessionFactory sessionFactory;
    
    public PaymentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Payment save(Payment payment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(payment);
            transaction.commit();
            return payment;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Payment> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Payment payment = session.get(Payment.class, id);
            return Optional.ofNullable(payment);
        }
    }
    
    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Payment> query = session.createQuery("FROM Payment WHERE transactionId = :transactionId", Payment.class);
            query.setParameter("transactionId", transactionId);
            return query.uniqueResultOptional();
        }
    }
    
    @Override
    public List<Payment> findByOrderId(Long orderId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Payment> query = session.createQuery("FROM Payment WHERE orderId = :orderId", Payment.class);
            query.setParameter("orderId", orderId);
            return query.list();
        }
    }
    
    @Override
    public List<Payment> findByStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Payment> query = session.createQuery("FROM Payment WHERE status = :status", Payment.class);
            query.setParameter("status", status);
            return query.list();
        }
    }
    
    @Override
    public List<Payment> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Payment", Payment.class).list();
        }
    }
    
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Payment payment = session.get(Payment.class, id);
            if (payment != null) {
                session.delete(payment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public void update(Payment payment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
