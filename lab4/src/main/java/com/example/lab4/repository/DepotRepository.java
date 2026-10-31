package com.example.lab4.repository;

import com.example.lab4.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepotRepository extends JpaRepository<Depot, Long> {
    java.util.Optional<Depot> findFirstByOrderByIdAsc();
    java.util.Optional<Depot> findByName(String name);
}
