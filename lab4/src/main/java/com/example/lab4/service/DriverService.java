package com.example.lab4.service;

import com.example.lab4.entity.Driver;
import com.example.lab4.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> findAll() { return driverRepository.findAll(); }
    public Optional<Driver> findById(Long id) { return driverRepository.findById(id); }
    public Optional<Driver> findByEmail(String email) { return driverRepository.findByEmail(email); }

    @Transactional
    public Driver save(Driver driver) { return driverRepository.save(driver); }

    @Transactional
    public void deleteById(Long id) { driverRepository.deleteById(id); }
}
