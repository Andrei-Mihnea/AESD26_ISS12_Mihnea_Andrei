package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDepotRepository extends JpaRepository<Depot, Long>, DepotRepository {
    java.util.Optional<Depot> findFirstByOrderByIdAsc();
    java.util.Optional<Depot> findByName(String name);
}
