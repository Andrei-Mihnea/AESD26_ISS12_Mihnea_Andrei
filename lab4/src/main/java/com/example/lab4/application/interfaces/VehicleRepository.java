package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.Vehicle;
import java.util.List;
import java.util.Optional;

/** Persistence port implemented by the infrastructure JPA adapter. */
public interface VehicleRepository {
    Optional<Vehicle> findById(Long id);
    List<Vehicle> findAll();
    <S extends Vehicle> S save(S entity);
    <S extends Vehicle> List<S> saveAll(Iterable<S> entities);
    void delete(Vehicle entity);
    void deleteById(Long id);
    void flush();
    long count();
    Optional<Vehicle> findByName(String name);
    List<Vehicle> findAvailableAtDepot(Long depotId);
    int updateAvailability(Long vehicleId, boolean available);
}
