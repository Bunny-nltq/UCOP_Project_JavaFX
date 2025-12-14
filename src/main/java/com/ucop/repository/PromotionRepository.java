package com.ucop.repository;

import com.ucop.entity.Promotion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PromotionRepository {
    private final SessionFactory sessionFactory;

    public PromotionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Promotion save(Promotion promotion) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(promotion);
            transaction.commit();
            return promotion;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving promotion: " + e.getMessage(), e);
        }
    }

    public Optional<Promotion> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Promotion promotion = session.get(Promotion.class, id);
            return Optional.ofNullable(promotion);
        } catch (Exception e) {
            throw new RuntimeException("Error finding promotion by id: " + e.getMessage(), e);
        }
    }

    public Optional<Promotion> findByCode(String code) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotion> query = session.createQuery(
                "FROM Promotion p WHERE p.code = :code", Promotion.class
            );
            query.setParameter("code", code);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            throw new RuntimeException("Error finding promotion by code: " + e.getMessage(), e);
        }
    }

    public List<Promotion> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Promotion p ORDER BY p.createdAt DESC", Promotion.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all promotions: " + e.getMessage(), e);
        }
    }

    public List<Promotion> findActivePromotions() {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotion> query = session.createQuery(
                "FROM Promotion p WHERE p.isActive = true " +
                "AND p.startDate <= :now AND p.endDate >= :now " +
                "ORDER BY p.createdAt DESC", 
                Promotion.class
            );
            query.setParameter("now", LocalDateTime.now());
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding active promotions: " + e.getMessage(), e);
        }
    }

    public List<Promotion> findByDiscountType(String discountType) {
        try (Session session = sessionFactory.openSession()) {
            Query<Promotion> query = session.createQuery(
                "FROM Promotion p WHERE p.discountType = :discountType " +
                "AND p.isActive = true ORDER BY p.createdAt DESC", 
                Promotion.class
            );
            query.setParameter("discountType", discountType);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error finding promotions by discount type: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Promotion promotion = session.get(Promotion.class, id);
            if (promotion != null) {
                session.delete(promotion);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting promotion: " + e.getMessage(), e);
        }
    }

    public void updateUsageCount(Long promotionId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery(
                "UPDATE Promotion p SET p.usageCount = p.usageCount + 1 WHERE p.id = :id"
            );
            query.setParameter("id", promotionId);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating usage count: " + e.getMessage(), e);
        }
    }
}
