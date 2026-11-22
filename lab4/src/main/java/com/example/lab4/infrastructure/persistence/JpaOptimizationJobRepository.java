package com.example.lab4.infrastructure.persistence;

import com.example.lab4.application.interfaces.OptimizationJobRepository;
import com.example.lab4.domain.entities.OptimizationJobRecord;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOptimizationJobRepository implements OptimizationJobRepository {
    private final SpringDataOptimizationJobRepository repository;

    public JpaOptimizationJobRepository(SpringDataOptimizationJobRepository repository) {
        this.repository = repository;
    }

    @Override public OptimizationJobRecord save(OptimizationJobRecord job) { return repository.save(job); }
    @Override public Optional<OptimizationJobRecord> findById(UUID requestId) { return repository.findById(requestId); }
}
