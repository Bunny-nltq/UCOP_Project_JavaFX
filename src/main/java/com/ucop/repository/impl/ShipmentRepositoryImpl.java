package com.ucop.repository.impl;

import com.ucop.entity.Shipment;
import com.ucop.repository.ShipmentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ShipmentRepositoryImpl implements ShipmentRepository {

    private final SessionFactory sessionFactory;

    public ShipmentRepositoryImpl(SessionFactory sessionFactory) {
        if (sessionFactory == null) throw new IllegalArgumentException("SessionFactory is null");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Shipment save(Shipment shipment) {
        if (shipment == null) throw new IllegalArgumentException("shipment is null");

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            // ✅ Hibernate 6: merge an toàn cho cả insert/update
            Shipment managed = session.merge(shipment);

            tx.commit();
            return managed;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<Shipment> findById(Long id) {
        if (id == null) return Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Shipment.class, id));
        }
    }

    @Override
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Shipment s where s.trackingNumber = :tn",
                            Shipment.class
                    )
                    .setParameter("tn", trackingNumber.trim())
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Shipment> findByOrderId(Long orderId) {
        if (orderId == null) return List.of();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Shipment s where s.order.id = :oid",
                            Shipment.class
                    )
                    .setParameter("oid", orderId)
                    .getResultList();
        }
    }

    @Override
    public List<Shipment> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) return List.of();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Shipment s where s.status = :st",
                            Shipment.class
                    )
                    .setParameter("st", status.trim())
                    .getResultList();
        }
    }

    @Override
    public List<Shipment> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Shipment", Shipment.class).getResultList();
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Shipment s = session.get(Shipment.class, id);
            if (s != null) {
                session.remove(s);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void update(Shipment shipment) {
        if (shipment == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(shipment); // ✅ merge thay cho update
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
