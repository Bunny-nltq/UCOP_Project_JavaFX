package com.ucop.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ucop.entity.AuditLog;
import com.ucop.util.HibernateUtil;

public class AuditLogDAO {

    /** SAVE LOG */
    public void save(AuditLog log) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.persist(log);  // Hibernate 6: persist() chuẩn
            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("❌ Error saving audit log");
            e.printStackTrace();
        }
    }

    /** GET ALL LOGS */
    public List<AuditLog> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "SELECT a FROM AuditLog a ORDER BY a.createdAt DESC",
                    AuditLog.class
            ).getResultList();

        } catch (Exception e) {
            System.err.println("❌ Error loading audit logs");
            e.printStackTrace();
            return List.of();
        }
    }

    /** DELETE LOG BY ID */
    public void delete(Long id) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            AuditLog log = session.find(AuditLog.class, id); // Hibernate 6 khuyến nghị find()
            if (log != null) {
                session.remove(log);
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("❌ Error deleting audit log");
            e.printStackTrace();
        }
    }
}
