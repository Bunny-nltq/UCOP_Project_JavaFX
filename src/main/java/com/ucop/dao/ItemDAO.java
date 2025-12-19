package com.ucop.dao;

import com.ucop.entity.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ItemDAO {

    private final SessionFactory sessionFactory;

    public ItemDAO(SessionFactory sessionFactory) {
        if (sessionFactory == null) throw new IllegalArgumentException("SessionFactory is null");
        this.sessionFactory = sessionFactory;
    }

    // ========================= CUSTOMER =========================

    public List<Item> findAllAvailable() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select i from Item i where i.status = 1",
                    Item.class
            ).getResultList();
        }
    }


    // Lấy sản phẩm theo danh mục + còn hàng
    public List<Item> findByCategory(Long categoryId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "select distinct i " +
                            "from Item i, StockItem si " +
                            "where si.itemId = i.id " +
                            "and (coalesce(si.onHand,0) - coalesce(si.reserved,0)) > 0 " +
                            "and i.category.id = :cid",
                    Item.class
            ).setParameter("cid", categoryId)
             .getResultList();
        }
    }

    // Search theo keyword + category + còn hàng
    public List<Item> search(String keyword, Long categoryId) {
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();

        String hql =
                "select distinct i " +
                        "from Item i, StockItem si " +
                        "where si.itemId = i.id " +
                        "and (coalesce(si.onHand,0) - coalesce(si.reserved,0)) > 0 " +
                        "and (:kw = '' or lower(i.name) like :likeKw or lower(i.sku) like :likeKw) " +
                        "and (:cid is null or i.category.id = :cid)";

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(hql, Item.class)
                    .setParameter("kw", kw)
                    .setParameter("likeKw", "%" + kw + "%")
                    .setParameter("cid", categoryId)
                    .getResultList();
        }
    }

    // ========================= COMMON / STAFF =========================

    public List<Item> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Item", Item.class).getResultList();
        }
    }

    public Item findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Item.class, id);
        }
    }

    // overload cho Long
    public Item findById(Long id) {
        if (id == null) return null;
        return findById(Integer.valueOf(id.toString()));
    }

    public Item findBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) return null;
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Item i where i.sku = :sku",
                            Item.class
                    ).setParameter("sku", sku.trim())
                     .uniqueResult();
        }
    }

    public void save(Item item) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(item);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void update(Item item) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(item);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void delete(Item item) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(item) ? item : session.merge(item));
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
