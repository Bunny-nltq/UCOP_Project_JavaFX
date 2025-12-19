package com.ucop.service;

import com.ucop.dao.ItemDAO;
import com.ucop.entity.Category;
import com.ucop.entity.Item;
import com.ucop.util.HibernateUtil;
import org.hibernate.SessionFactory;

import java.util.List;

public class ItemService {

    private final ItemDAO dao;
    private final AuditLogService audit = new AuditLogService();

    // constructor mặc định
    public ItemService() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        this.dao = new ItemDAO(sf);
    }

    // DI
    public ItemService(ItemDAO dao) {
        if (dao == null) throw new IllegalArgumentException("ItemDAO is null");
        this.dao = dao;
    }

    // ========================= CUSTOMER =========================

    public List<Item> findForCustomer() {
        List<Item> items = dao.findAllAvailable();
        System.out.println("[DEBUG] findForCustomer: " + items.size() + " items loaded");
        return items;
    }

    /**
     * ✅ FIX: categoryId dùng Long (khớp DB bigint)
     */
    public List<Item> findForCustomerByCategory(Long categoryId) {
        List<Item> items = (categoryId == null)
                ? dao.findAllAvailable()
                : dao.findByCategory(categoryId);

        System.out.println("[DEBUG] findForCustomerByCategory(" + categoryId + "): " + items.size() + " items loaded");
        return items;
    }

    /**
     * ✅ FIX: categoryId dùng Long (khớp DB bigint)
     */
    public List<Item> searchForCustomer(String keyword, Long categoryId) {
        List<Item> items = dao.search(keyword, categoryId);
        System.out.println("[DEBUG] searchForCustomer('" + keyword + "', " + categoryId + "): " + items.size() + " items found");
        return items;
    }

    // ========================= COMMON =========================

    /**
     * ✅ FIX: Item.id là Long
     */
    public Item getItemById(Long id) {
        return (id == null) ? null : dao.findById(id);
    }

    // ========================= STAFF =========================

    public List<Item> findAll() {
        return dao.findAll();
    }

    public void save(Item item) {
        if (dao.findBySku(item.getSku()) != null) {
            throw new IllegalArgumentException("SKU đã tồn tại");
        }
        dao.save(item);
    }

    public void update(Item item) {
        dao.update(item);
    }

    public void delete(Item item) {
        dao.delete(item);
    }

    // ========================= HELPER =========================
    // Nếu chỗ nào đó bạn vẫn có categoryId kiểu Integer, dùng helper này để convert:
    public static Long toLongId(Object id) {
        if (id == null) return null;
        if (id instanceof Long) return (Long) id;
        if (id instanceof Integer) return ((Integer) id).longValue();
        if (id instanceof String) return Long.parseLong((String) id);
        throw new IllegalArgumentException("Unsupported id type: " + id.getClass());
    }
}
