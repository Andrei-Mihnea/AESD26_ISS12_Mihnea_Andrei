package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.entities.OptimizationJobRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataOptimizationJobRepository extends JpaRepository<OptimizationJobRecord, UUID> {}
