package com.ucop.repository.impl;

import com.ucop.entity.Cart;
import com.ucop.repository.CartRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CartRepositoryImpl implements CartRepository {

    private final SessionFactory sessionFactory;

    public CartRepositoryImpl(SessionFactory sessionFactory) {
        if (sessionFactory == null) throw new IllegalArgumentException("SessionFactory is null");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Cart save(Cart cart) {
        if (cart == null) throw new IllegalArgumentException("cart is null");

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            // ✅ merge: an toàn cả insert/update
            Cart managed = session.merge(cart);

            tx.commit();
            return managed;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<Cart> findById(Long id) {
        if (id == null) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            Cart cart = session.createQuery(
                            "select distinct c from Cart c " +
                                    "left join fetch c.items " +
                                    "where c.id = :id",
                            Cart.class
                    )
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(cart);
        }
    }

    @Override
    public Optional<Cart> findByAccountId(Long accountId) {
        if (accountId == null) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "select distinct c from Cart c " +
                                    "left join fetch c.items " +
                                    "where c.accountId = :aid",
                            Cart.class
                    )
                    .setParameter("aid", accountId)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Cart> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Cart", Cart.class).getResultList();
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Cart cart = session.get(Cart.class, id);
            if (cart != null) {
                session.remove(cart);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void update(Cart cart) {
        if (cart == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(cart);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
