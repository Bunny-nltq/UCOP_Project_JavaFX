package com.ucop.dao;

import com.ucop.entity.Item;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ItemDAO extends GenericDAO<Item> {

    public ItemDAO() {
        super(Item.class);
    }

    // Tìm theo SKU
    public Item findBySku(String sku) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Item WHERE sku = :sku", Item.class
            )
            .setParameter("sku", sku)
            .uniqueResult();
        }
    }

    // Tìm Item theo Category
    public List<Item> findByCategory(int categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Item WHERE category.id = :cid ORDER BY name ASC",
                    Item.class
            )
            .setParameter("cid", categoryId)
            .list();
        }
    }
}
