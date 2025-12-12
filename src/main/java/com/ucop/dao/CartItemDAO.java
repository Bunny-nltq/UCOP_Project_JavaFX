package com.ucop.dao;

import com.ucop.entity.CartItem;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

/**
 * DAO for CartItem entity
 * Handles data access operations for shopping cart items
 */
public class CartItemDAO extends GenericDAO<CartItem> {

    public CartItemDAO() {
        super(CartItem.class);
    }

    /**
     * Find all items in a specific cart
     * @param cartId - ID of the cart
     * @return List of CartItem objects in the cart
     */
    public List<CartItem> findByCartId(Long cartId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM CartItem WHERE cart.id = :cartId ORDER BY id ASC",
                    CartItem.class
            )
            .setParameter("cartId", cartId)
            .list();
        }
    }

    /**
     * Find a specific cart item by cart ID and item ID
     * @param cartId - ID of the cart
     * @param itemId - ID of the item
     * @return CartItem if found, null otherwise
     */
    public CartItem findByCartIdAndItemId(Long cartId, Long itemId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM CartItem WHERE cart.id = :cartId AND itemId = :itemId",
                    CartItem.class
            )
            .setParameter("cartId", cartId)
            .setParameter("itemId", itemId)
            .uniqueResult();
        }
    }

    /**
     * Get total quantity of items in a cart
     * @param cartId - ID of the cart
     * @return Total quantity of items
     */
    public int getTotalQuantityByCartId(Long cartId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long totalQuantity = session.createQuery(
                    "SELECT SUM(quantity) FROM CartItem WHERE cart.id = :cartId",
                    Long.class
            )
            .setParameter("cartId", cartId)
            .uniqueResult();
            return totalQuantity != null ? totalQuantity.intValue() : 0;
        }
    }

    /**
     * Count number of items in a cart
     * @param cartId - ID of the cart
     * @return Number of item types in the cart
     */
    public long countByCartId(Long cartId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(*) FROM CartItem WHERE cart.id = :cartId",
                    Long.class
            )
            .setParameter("cartId", cartId)
            .uniqueResult();
            return count != null ? count : 0;
        }
    }

    /**
     * Delete all items from a cart
     * @param cartId - ID of the cart
     */
    public void deleteAllByCartId(Long cartId) {
        var tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery(
                    "DELETE FROM CartItem WHERE cart.id = :cartId"
            )
            .setParameter("cartId", cartId)
            .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Update quantity for a cart item
     * @param cartItemId - ID of the cart item
     * @param newQuantity - New quantity value
     */
    public void updateQuantity(Long cartItemId, int newQuantity) {
        var tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery(
                    "UPDATE CartItem SET quantity = :qty WHERE id = :id"
            )
            .setParameter("qty", newQuantity)
            .setParameter("id", cartItemId)
            .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
