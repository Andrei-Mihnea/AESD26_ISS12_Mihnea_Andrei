package com.example.lab4.service;

import com.example.lab4.entity.Customer;
import com.example.lab4.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() { return customerRepository.findAll(); }
    public Optional<Customer> findById(Long id) { return customerRepository.findById(id); }
    public Optional<Customer> findByEmail(String email) { return customerRepository.findByEmail(email); }

    @Transactional
    public Customer save(Customer customer) { return customerRepository.save(customer); }

    @Transactional
    public void deleteById(Long id) { customerRepository.deleteById(id); }
}
