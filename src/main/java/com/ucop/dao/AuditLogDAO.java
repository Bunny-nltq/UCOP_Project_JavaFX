package com.ucop.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ucop.entity.AuditLog;
import com.ucop.util.HibernateUtil;

public class AuditLogDAO {

    public void save(AuditLog log) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(log);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<AuditLog> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from AuditLog order by timestamp desc", AuditLog.class).list();
        }
    }
}
