package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.OptimizationJobRecord;

import java.util.Optional;
import java.util.UUID;

public interface OptimizationJobRepository {
    OptimizationJobRecord save(OptimizationJobRecord job);
    Optional<OptimizationJobRecord> findById(UUID requestId);
}
