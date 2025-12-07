package com.ucop.repository;

import com.ucop.entity.PromotionUsage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

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
            throw new RuntimeException("Error saving promotion usage: " + e.getMessage(), e);
        }
    }

    public Optional<PromotionUsage> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            PromotionUsage usage = session.get(PromotionUsage.class, id);
            return Optional.ofNullable(usage);
        } catch (Exception e) {
            throw new RuntimeException("Error finding promotion usage by id: " + e.getMessage(), e);
        }
    }

    public List<PromotionUsage> findByPromotionId(Long promotionId) {
        try (Session session = sessionFactory.openSession()) {
            Query<PromotionUsage> query = session.createQuery(
                "FROM PromotionUsage pu WHERE pu.promotion.id = :promotionId ORDER BY pu.usedAt DESC",
                PromotionUsage.class
            );
            query.setParameter("promotionId", promotionId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding usages by promotion: " + e.getMessage(), e);
        }
    }

    public List<PromotionUsage> findByAccountId(Long accountId) {
        try (Session session = sessionFactory.openSession()) {
            Query<PromotionUsage> query = session.createQuery(
                "FROM PromotionUsage pu WHERE pu.accountId = :accountId ORDER BY pu.usedAt DESC",
                PromotionUsage.class
            );
            query.setParameter("accountId", accountId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding usages by account: " + e.getMessage(), e);
        }
    }

    public int countByPromotionAndAccount(Long promotionId, Long accountId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(pu) FROM PromotionUsage pu " +
                "WHERE pu.promotion.id = :promotionId AND pu.accountId = :accountId",
                Long.class
            );
            query.setParameter("promotionId", promotionId);
            query.setParameter("accountId", accountId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            throw new RuntimeException("Error counting usages: " + e.getMessage(), e);
        }
    }

    public List<PromotionUsage> findByOrderId(Long orderId) {
        try (Session session = sessionFactory.openSession()) {
            Query<PromotionUsage> query = session.createQuery(
                "FROM PromotionUsage pu WHERE pu.order.id = :orderId",
                PromotionUsage.class
            );
            query.setParameter("orderId", orderId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding usages by order: " + e.getMessage(), e);
        }
    }
}
