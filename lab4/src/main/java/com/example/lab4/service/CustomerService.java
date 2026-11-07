package com.example.lab4.service;

import com.example.lab4.entity.Role;
import com.example.lab4.entity.User;
import com.example.lab4.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomerService {
    private final UserRepository users;
    public CustomerService(UserRepository users) { this.users = users; }

    public List<User> findAll() { return users.findAllByRole(Role.CUSTOMER); }
    public Optional<User> findById(Long id) { return users.findByIdAndRole(id, Role.CUSTOMER); }
    @Cacheable(cacheNames = "customersByEmail", key = "#email")
    public Optional<User> findByEmail(String email) { return users.findByEmailAndRole(email, Role.CUSTOMER); }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public User save(User customer) {
        customer.requireRole(Role.CUSTOMER);
        return users.save(customer);
    }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public List<User> saveAll(Iterable<User> customers) {
        List<User> selected = new ArrayList<>();
        customers.forEach(customer -> { customer.requireRole(Role.CUSTOMER); selected.add(customer); });
        return users.saveAll(selected);
    }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public void deleteById(Long id) {
        users.findByIdAndRole(id, Role.CUSTOMER).ifPresent(users::delete);
    }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public Optional<User> update(Long id, String name, String email, String phone, String address, String coordinates) {
        return users.findByIdAndRole(id, Role.CUSTOMER).map(customer -> {
            customer.updateCustomerDetails(name, email, phone, address, coordinates);
            return customer;
        });
    }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public boolean deleteIfPresent(Long id) {
        return users.findByIdAndRole(id, Role.CUSTOMER).map(customer -> {
            users.delete(customer);
            users.flush();
            return true;
        }).orElse(false);
    }
}
