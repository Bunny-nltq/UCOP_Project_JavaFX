package com.ucop.repository.impl;

import com.ucop.entity.Shipment;
import com.ucop.repository.ShipmentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ShipmentRepositoryImpl implements ShipmentRepository {
    
    private final SessionFactory sessionFactory;
    
    public ShipmentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Shipment save(Shipment shipment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(shipment);
            transaction.commit();
            return shipment;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Shipment> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Shipment shipment = session.get(Shipment.class, id);
            return Optional.ofNullable(shipment);
        }
    }
    
    @Override
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Shipment> query = session.createQuery(
                "FROM Shipment WHERE trackingNumber = :trackingNumber", Shipment.class);
            query.setParameter("trackingNumber", trackingNumber);
            return query.uniqueResultOptional();
        }
    }
    
    @Override
    public List<Shipment> findByOrderId(Long orderId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Shipment> query = session.createQuery(
                "FROM Shipment WHERE order.id = :orderId", Shipment.class);
            query.setParameter("orderId", orderId);
            return query.list();
        }
    }
    
    @Override
    public List<Shipment> findByStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Shipment> query = session.createQuery(
                "FROM Shipment WHERE status = :status", Shipment.class);
            query.setParameter("status", status);
            return query.list();
        }
    }
    
    @Override
    public List<Shipment> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Shipment", Shipment.class).list();
        }
    }
    
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Shipment shipment = session.get(Shipment.class, id);
            if (shipment != null) {
                session.delete(shipment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public void update(Shipment shipment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(shipment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
