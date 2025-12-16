package com.ucop.repository;

import com.ucop.entity.Warehouse;
import java.util.Optional;
import java.util.List;

public interface WarehouseRepository {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(Long id);
    List<Warehouse> findAll();
    List<Warehouse> findAllActive();
    void delete(Long id);
    void update(Warehouse warehouse);
}
