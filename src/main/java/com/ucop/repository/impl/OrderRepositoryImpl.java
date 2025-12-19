package com.ucop.repository.impl;

import com.ucop.entity.Order;
import com.ucop.repository.OrderRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl implements OrderRepository {

    private final SessionFactory sessionFactory;

    public OrderRepositoryImpl(SessionFactory sessionFactory) {
        if (sessionFactory == null) throw new IllegalArgumentException("SessionFactory is null");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Order save(Order order) {
        if (order == null) throw new IllegalArgumentException("order is null");

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            // ✅ merge dùng được cho cả insert/update
            Order managed = session.merge(order);

            tx.commit();
            return managed;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        if (id == null) return Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Order.class, id));
        }
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Order o where o.orderNumber = :on",
                            Order.class
                    )
                    .setParameter("on", orderNumber.trim())
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Order> findByAccountId(Long accountId) {
        if (accountId == null) return List.of();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Order o where o.accountId = :aid order by o.placedAt desc",
                            Order.class
                    )
                    .setParameter("aid", accountId)
                    .getResultList();
        }
    }

    /**
     * ✅ FIX: status là enum trong entity -> nhận String thì parse, nhận sai trả list rỗng
     */
    @Override
    public List<Order> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) return List.of();

        Order.OrderStatus st;
        try {
            st = Order.OrderStatus.valueOf(status.trim());
        } catch (IllegalArgumentException e) {
            return List.of();
        }

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Order o where o.status = :st order by o.placedAt desc",
                            Order.class
                    )
                    .setParameter("st", st)
                    .getResultList();
        }
    }

    @Override
    public List<Order> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Order o order by o.placedAt desc", Order.class).getResultList();
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Order o = session.get(Order.class, id);
            if (o != null) {
                session.remove(o);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void update(Order order) {
        if (order == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(order); // ✅ merge thay cho update
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
