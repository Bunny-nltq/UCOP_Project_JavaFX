package com.ucop.repository.impl;

import com.ucop.entity.Order;
import com.ucop.repository.OrderRepository;
import org.hibernate.Hibernate;
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
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(order);
            tx.commit();
            return order;
        } catch (Exception e) {
            try {
                if (tx != null && tx.isActive()) tx.rollback();
            } catch (Exception ignore) {}
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

    /**
     * ✅ FIX: fetch join items + force initialize trước khi đóng session
     * => order.getItems() sẽ không còn LazyInitializationException sau khi repo return
     */
    public Optional<Order> findByIdWithItems(Long id) {
        if (id == null) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            String hql = "select distinct o from Order o left join fetch o.items where o.id = :id";
            Order o = session.createQuery(hql, Order.class)
                    .setParameter("id", id)
                    .uniqueResult();

            if (o != null) {
                // force init collection items (chắc chắn 100% không còn proxy sau khi session đóng)
                Hibernate.initialize(o.getItems());
            }

            return Optional.ofNullable(o);
        }
    }

    /**
     * (Optional) Nếu bạn muốn chạy trong transaction (an toàn hơn khi mapping phức tạp)
     */
    public Optional<Order> findByIdWithItemsAndTx(Long id) {
        if (id == null) return Optional.empty();

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            String hql = "select distinct o from Order o left join fetch o.items where o.id = :id";
            Order o = session.createQuery(hql, Order.class)
                    .setParameter("id", id)
                    .uniqueResult();

            if (o != null) Hibernate.initialize(o.getItems());

            tx.commit();
            return Optional.ofNullable(o);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
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
            if (o != null) session.remove(o);

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public void update(Order order) {
        if (order == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
