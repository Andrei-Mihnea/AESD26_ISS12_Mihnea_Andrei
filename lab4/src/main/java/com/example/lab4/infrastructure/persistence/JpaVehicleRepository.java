package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaVehicleRepository extends JpaRepository<Vehicle, Long>, VehicleRepository {
    java.util.Optional<Vehicle> findByName(String name);

    @Query("select v from Vehicle v where v.depot.id = :depotId and v.available = true")
    List<Vehicle> findAvailableAtDepot(@Param("depotId") Long depotId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update Vehicle v set v.available = :available where v.id = :vehicleId")
    int updateAvailability(@Param("vehicleId") Long vehicleId,
                           @Param("available") boolean available);
}
