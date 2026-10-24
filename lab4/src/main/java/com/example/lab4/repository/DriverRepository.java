package com.example.lab4.repository;

import com.example.lab4.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    java.util.Optional<Driver> findByEmail(String email);
}
