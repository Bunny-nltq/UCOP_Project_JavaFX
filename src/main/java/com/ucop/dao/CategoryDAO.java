package com.ucop.dao;

import com.ucop.entity.Category;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CategoryDAO {

    // SAVE (persist)
    public void save(Category cat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(cat);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // UPDATE (merge - an toàn hơn update)
    public void update(Category cat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(cat);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // DELETE category - kiểm tra con
    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Category cat = session.get(Category.class, id);
            if (cat == null) return false;

            // Không cho xóa nếu có category con
            if (cat.getChildren() != null && !cat.getChildren().isEmpty()) {
                return false;
            }

            tx = session.beginTransaction();
            session.remove(cat);
            tx.commit();

            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // FIND BY ID
    public Category findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Category.class, id);
        }
    }

    // FIND ALL
    public List<Category> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Category ORDER BY name ASC", Category.class
            ).list();
        }
    }

    // FIND ROOT CATEGORIES (parent IS NULL)
    public List<Category> findRoot() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Category WHERE parent IS NULL ORDER BY name ASC", Category.class
            ).list();
        }
    }

    // FIND CHILDREN CATEGORY
    public List<Category> findChildren(int parentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Category WHERE parent.id = :pid ORDER BY name ASC", Category.class
            ).setParameter("pid", parentId).list();
        }
    }
}
