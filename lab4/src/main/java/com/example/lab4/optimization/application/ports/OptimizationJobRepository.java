package com.example.lab4.optimization.application.ports;

import com.example.lab4.optimization.domain.OptimizationJobRecord;

import java.util.Optional;
import java.util.UUID;

public interface OptimizationJobRepository {
    OptimizationJobRecord save(OptimizationJobRecord job);
    Optional<OptimizationJobRecord> findById(UUID requestId);
}
