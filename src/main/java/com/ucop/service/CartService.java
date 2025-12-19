package com.ucop.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.repository.CartRepository;
import com.ucop.util.HibernateUtil;

/**
 * Service quản lý giỏ hàng
 */
public class CartService {

    private final CartRepository cartRepository;
    private final SessionFactory sessionFactory;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Lấy hoặc tạo Cart cho account
     */
    public Cart getOrCreateCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseGet(() -> cartRepository.save(new Cart(accountId)));
    }

    /**
     * Thêm item vào giỏ hàng
     */
    public void addToCart(Long cartId, Long itemId, int quantity, BigDecimal unitPrice) {
        if (cartId == null || itemId == null || quantity <= 0) {
            throw new IllegalArgumentException("Tham số không hợp lệ");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            // Kiểm tra item đã có trong cart chưa
            Optional<CartItem> existing = cart.getItems().stream()
                    .filter(ci -> ci.getItemId().equals(itemId))
                    .findFirst();

            if (existing.isPresent()) {
                // Update số lượng item tồn tại
                CartItem ci = existing.get();
                ci.setQuantity(ci.getQuantity() + quantity);
                session.merge(ci);
            } else {
                // Thêm item mới
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setItemId(itemId);
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(unitPrice);
                cart.addItem(newItem);
                session.persist(newItem); // dùng persist cho item mới
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage(), e);
        }
    }


    /**
     * Cập nhật số lượng item trong giỏ hàng
     */
    public void updateItemQuantity(Long cartId, Long itemId, int quantity) {
        if (cartId == null || itemId == null) return;

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        cart.getItems().stream()
                .filter(ci -> ci.getItemId().equals(itemId))
                .findFirst()
                .ifPresent(ci -> ci.setQuantity(quantity));

        cartRepository.update(cart);
    }

    /**
     * Xóa item khỏi giỏ hàng
     */
    public void removeItemFromCart(Long cartId, Long itemId) {
        if (cartId == null || itemId == null) return;

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        cart.getItems().removeIf(ci -> ci.getItemId().equals(itemId));
        cartRepository.update(cart);
    }

    /**
     * Xóa toàn bộ cart
     */
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        cart.clearCart();
        cartRepository.update(cart);
    }

    /**
     * Tính tổng tiền cart
     */
    public BigDecimal calculateCartTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            if (ci.getUnitPrice() != null) {
                total = total.add(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
            }
        }
        return total;
    }

    /**
     * Đếm số lượng item trong cart
     */
    public int getCartItemCount(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        return cart != null ? cart.getItems().size() : 0;
    }
}
