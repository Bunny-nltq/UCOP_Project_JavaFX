package com.ucop.repository;

import com.ucop.entity.StockItem;
import java.util.Optional;
import java.util.List;

public interface StockItemRepository {
    StockItem save(StockItem stockItem);
    Optional<StockItem> findById(Long id);
    Optional<StockItem> findByWarehouseAndItem(Long warehouseId, Long itemId);
    List<StockItem> findByWarehouseId(Long warehouseId);
    List<StockItem> findLowStock();
    List<StockItem> findAll();
    void delete(Long id);
    void update(StockItem stockItem);
}
