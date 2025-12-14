package com.ucop.repository.impl;

import com.ucop.entity.StockItem;
import com.ucop.repository.StockItemRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StockItemRepositoryImpl implements StockItemRepository {
    
    private final SessionFactory sessionFactory;
    
    public StockItemRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public StockItem save(StockItem stockItem) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(stockItem);
            transaction.commit();
            return stockItem;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<StockItem> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            StockItem stockItem = session.get(StockItem.class, id);
            return Optional.ofNullable(stockItem);
        }
    }
    
    @Override
    public Optional<StockItem> findByWarehouseAndItem(Long warehouseId, Long itemId) {
        try (Session session = sessionFactory.openSession()) {
            Query<StockItem> query = session.createQuery(
                "FROM StockItem WHERE warehouseId = :warehouseId AND itemId = :itemId", 
                StockItem.class);
            query.setParameter("warehouseId", warehouseId);
            query.setParameter("itemId", itemId);
            return query.uniqueResultOptional();
        }
    }
    
    @Override
    public List<StockItem> findByWarehouseId(Long warehouseId) {
        try (Session session = sessionFactory.openSession()) {
            Query<StockItem> query = session.createQuery("FROM StockItem WHERE warehouseId = :warehouseId", StockItem.class);
            query.setParameter("warehouseId", warehouseId);
            return query.list();
        }
    }
    
    @Override
    public List<StockItem> findLowStock() {
        try (Session session = sessionFactory.openSession()) {
            Query<StockItem> query = session.createQuery("FROM StockItem WHERE quantity < 10", StockItem.class);
            return query.list();
        }
    }
    
    @Override
    public List<StockItem> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM StockItem", StockItem.class).list();
        }
    }
    
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            StockItem stockItem = session.get(StockItem.class, id);
            if (stockItem != null) {
                session.delete(stockItem);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public void update(StockItem stockItem) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(stockItem);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
