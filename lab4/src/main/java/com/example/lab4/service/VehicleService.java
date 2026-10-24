package com.example.lab4.service;

import com.example.lab4.entity.Vehicle;
import com.example.lab4.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> findAll() { return vehicleRepository.findAll(); }
    public Optional<Vehicle> findById(Long id) { return vehicleRepository.findById(id); }
    public Optional<Vehicle> findByName(String name) { return vehicleRepository.findByName(name); }
    public List<Vehicle> findAvailableAtDepot(Long depotId) {
        return vehicleRepository.findAvailableAtDepot(depotId);
    }

    @Transactional
    public Vehicle save(Vehicle vehicle) { return vehicleRepository.save(vehicle); }

    @Transactional
    public void deleteById(Long id) { vehicleRepository.deleteById(id); }

    @Transactional
    public int updateAvailability(Long vehicleId, boolean available) {
        return vehicleRepository.updateAvailability(vehicleId, available);
    }
}
