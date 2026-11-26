package com.example.lab4.optimization.infrastructure.persistence;

import com.example.lab4.optimization.domain.OptimizationJobRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataOptimizationJobRepository extends JpaRepository<OptimizationJobRecord, UUID> {}
