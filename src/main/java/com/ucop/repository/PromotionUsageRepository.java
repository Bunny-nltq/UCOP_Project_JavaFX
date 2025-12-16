package com.ucop.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.ucop.entity.PromotionUsage;

public class PromotionUsageRepository {

    private final SessionFactory sessionFactory;

    public PromotionUsageRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public PromotionUsage save(PromotionUsage usage) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(usage);
            transaction.commit();
            return usage;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<PromotionUsage> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            PromotionUsage usage = session.get(PromotionUsage.class, id);
            return Optional.ofNullable(usage);
        }
    }

    public List<PromotionUsage> findByPromotionId(Long promotionId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM PromotionUsage WHERE promotion.id = :promotionId", PromotionUsage.class)
                    .setParameter("promotionId", promotionId)
                    .getResultList();
        }
    }

    public long countByPromotionAndAccount(Long promotionId, Long accountId) {
        try (Session session = sessionFactory.openSession()) {
            return (Long) session.createQuery("SELECT COUNT(u) FROM PromotionUsage u WHERE u.promotion.id = :promotionId AND u.accountId = :accountId")
                    .setParameter("promotionId", promotionId)
                    .setParameter("accountId", accountId)
                    .uniqueResult();
        }
    }

    public void update(PromotionUsage usage) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(usage);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            PromotionUsage usage = session.get(PromotionUsage.class, id);
            if (usage != null) {
                session.delete(usage);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
