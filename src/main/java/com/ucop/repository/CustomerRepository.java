package com.ucop.repository;

import com.ucop.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByAccountId(Long accountId);
    List<Customer> findAll();
    void delete(Customer customer);
    void deleteById(Long id);
    long count();
}
