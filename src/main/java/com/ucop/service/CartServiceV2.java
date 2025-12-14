package com.ucop.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.ucop.entity.Cart;
import com.ucop.entity.CartItem;
import com.ucop.entity.Product;
import com.ucop.dao.ProductDAO;
import com.ucop.repository.CartRepository;
import com.ucop.util.HibernateUtil;

/**
 * Service V2 cho Cart - làm việc với Product entity
 */
public class CartServiceV2 {
    
    private final CartRepository cartRepository;
    private final ProductDAO productDAO;
    private final SessionFactory sessionFactory;

    public CartServiceV2(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
        this.productDAO = new ProductDAO();
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Lấy hoặc tạo cart cho account
     */
    public Cart getOrCreateCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseGet(() -> cartRepository.save(new Cart(accountId)));
    }

    /**
     * Thêm product vào cart
     */
    public void addProductToCart(Long cartId, Long productId, int quantity) {
        if (cartId == null || productId == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        // Kiểm tra product tồn tại và còn hàng
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại!");
        }

        Product product = productOpt.get();
        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Sản phẩm không còn kinh doanh!");
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Không đủ số lượng trong kho!");
        }

        // Lấy cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));

        // Kiểm tra xem product đã có trong cart chưa
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getItemId().equals(productId))
                .findFirst();

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            if (existingItem.isPresent()) {
                // Cập nhật số lượng
                CartItem item = existingItem.get();
                int newQuantity = item.getQuantity() + quantity;
                
                // Kiểm tra lại stock
                if (product.getStockQuantity() < newQuantity) {
                    throw new IllegalArgumentException("Không đủ số lượng trong kho! Tối đa: " + product.getStockQuantity());
                }
                
                item.setQuantity(newQuantity);
                session.merge(item);
            } else {
                // Thêm item mới
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setItemId(productId);
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(product.getPrice());
                cart.addItem(newItem);
                session.merge(cart);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa product khỏi cart
     */
    public void removeProductFromCart(Long cartId, Long productId) {
        if (cartId == null || productId == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));

        cart.getItems().removeIf(item -> item.getItemId().equals(productId));
        cartRepository.update(cart);
    }

    /**
     * Cập nhật số lượng product trong cart
     */
    public void updateProductQuantity(Long cartId, Long productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeProductFromCart(cartId, productId);
            return;
        }

        // Kiểm tra stock
        Optional<Product> productOpt = productDAO.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getStockQuantity() == null || product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Không đủ số lượng trong kho! Tối đa: " + 
                    (product.getStockQuantity() != null ? product.getStockQuantity() : 0));
            }
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));

        cart.getItems().stream()
                .filter(item -> item.getItemId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity));

        cartRepository.update(cart);
    }

    /**
     * Xóa toàn bộ cart
     */
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));

        cart.clearCart();
        cartRepository.update(cart);
    }

    /**
     * Tính tổng tiền cart
     */
    public BigDecimal calculateCartTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại!"));

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            if (item.getUnitPrice() != null) {
                BigDecimal itemTotal = item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    /**
     * Lấy số lượng items trong cart
     */
    public int getCartItemCount(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        return cart != null ? cart.getItemCount() : 0;
    }
}
