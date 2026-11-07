package com.example.lab4.service;

import com.example.lab4.entity.Role;
import com.example.lab4.entity.User;
import com.example.lab4.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DriverService {
    private final UserRepository users;
    public DriverService(UserRepository users) { this.users = users; }
    public List<User> findAll() { return users.findAllByRole(Role.DRIVER); }
    public Optional<User> findById(Long id) { return users.findByIdAndRole(id, Role.DRIVER); }
    public Optional<User> findByEmail(String email) { return users.findByEmailAndRole(email, Role.DRIVER); }

    @Transactional
    public User save(User driver) {
        driver.requireRole(Role.DRIVER);
        return users.save(driver);
    }

    @Transactional
    public List<User> saveAll(Iterable<User> drivers) {
        List<User> selected = new ArrayList<>();
        drivers.forEach(driver -> { driver.requireRole(Role.DRIVER); selected.add(driver); });
        return users.saveAll(selected);
    }

    @Transactional
    public void deleteById(Long id) {
        users.findByIdAndRole(id, Role.DRIVER).ifPresent(driver -> {
            if (driver.getVehicle() != null) driver.getVehicle().unassignDriver();
            users.delete(driver);
        });
    }
}
