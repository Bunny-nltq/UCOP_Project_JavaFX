package com.ucop.repository.impl;

import com.ucop.entity.StockItem;
import com.ucop.repository.StockItemRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class StockItemRepositoryImpl implements StockItemRepository {

    private final SessionFactory sessionFactory;

    public StockItemRepositoryImpl(SessionFactory sessionFactory) {
        if (sessionFactory == null) throw new IllegalArgumentException("SessionFactory is null");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public StockItem save(StockItem stockItem) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(stockItem);
            tx.commit();
            return stockItem;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<StockItem> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(StockItem.class, id));
        }
    }

    @Override
    public Optional<StockItem> findByWarehouseAndItem(Long warehouseId, Long itemId) {
        if (warehouseId == null || itemId == null) return Optional.empty();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from StockItem si " +
                                    "where si.warehouse.id = :wid " +
                                    "and si.itemId = :iid",
                            StockItem.class
                    )
                    .setParameter("wid", warehouseId)
                    .setParameter("iid", itemId)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<StockItem> findByWarehouseId(Long warehouseId) {
        if (warehouseId == null) return List.of();

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from StockItem si where si.warehouse.id = :wid",
                            StockItem.class
                    )
                    .setParameter("wid", warehouseId)
                    .getResultList();
        }
    }

    @Override
    public List<StockItem> findLowStock() {
        try (Session session = sessionFactory.openSession()) {
            // low stock khi onHand <= lowStockThreshold hoặc isLowStock = true
            return session.createQuery(
                    "from StockItem si " +
                            "where si.isLowStock = true " +
                            "or coalesce(si.onHand,0) <= coalesce(si.lowStockThreshold,0)",
                    StockItem.class
            ).getResultList();
        }
    }

    @Override
    public List<StockItem> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from StockItem", StockItem.class).getResultList();
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            StockItem stockItem = session.get(StockItem.class, id);
            if (stockItem != null) {
                session.remove(stockItem);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void update(StockItem stockItem) {
        if (stockItem == null) return;

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(stockItem);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
