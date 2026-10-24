package com.example.lab4.service;

import com.example.lab4.entity.Customer;
import com.example.lab4.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() { return customerRepository.findAll(); }
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) { return customerRepository.findById(id); }
    @Cacheable(cacheNames = "customersByEmail", key = "#email")
    public Optional<Customer> findByEmail(String email) { return customerRepository.findByEmail(email); }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public Customer save(Customer customer) { return customerRepository.save(customer); }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public List<Customer> saveAll(Iterable<Customer> customers) { return customerRepository.saveAll(customers); }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public void deleteById(Long id) { customerRepository.deleteById(id); }
}
