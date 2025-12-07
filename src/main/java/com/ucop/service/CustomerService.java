package com.ucop.service;

import com.ucop.entity.Customer;
import com.ucop.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing customers
 */
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Create or update customer
     */
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Get customer by account ID
     */
    public Optional<Customer> getCustomerByAccountId(Long accountId) {
        return customerRepository.findByAccountId(accountId);
    }

    /**
     * Get or create customer for account
     */
    public Customer getOrCreateCustomer(Long accountId, String fullName, String email) {
        Optional<Customer> existing = customerRepository.findByAccountId(accountId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Customer newCustomer = new Customer(accountId, fullName, email, null);
        return customerRepository.save(newCustomer);
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Update customer information
     */
    public Customer updateCustomer(Long id, Customer customerData) {
        Optional<Customer> existingOpt = customerRepository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }

        Customer existing = existingOpt.get();
        existing.setFullName(customerData.getFullName());
        existing.setEmail(customerData.getEmail());
        existing.setPhone(customerData.getPhone());
        existing.setAddress(customerData.getAddress());
        existing.setCity(customerData.getCity());
        existing.setPostalCode(customerData.getPostalCode());

        return customerRepository.save(existing);
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    /**
     * Count total customers
     */
    public long countCustomers() {
        return customerRepository.count();
    }
}
