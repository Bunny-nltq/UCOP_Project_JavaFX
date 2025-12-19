package com.ucop.repository;

import com.ucop.entity.Cart;
import java.util.Optional;
import java.util.List;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findById(Long id);
    Optional<Cart> findByAccountId(Long accountId);
    List<Cart> findAll();
    void delete(Long id);
    void update(Cart cart);
}
