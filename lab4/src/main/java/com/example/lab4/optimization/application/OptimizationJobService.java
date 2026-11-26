package com.example.lab4.optimization.application;

import com.example.lab4.application.exceptions.OperationConflictException;
import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.optimization.application.ports.OptimizationJobRepository;
import com.example.lab4.optimization.domain.OptimizationJobRecord;
import com.example.vros.messaging.OptimizationAcknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class OptimizationJobService {
    private final OptimizationJobRepository jobs;

    public OptimizationJobService(OptimizationJobRepository jobs) { this.jobs = jobs; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OptimizationJobRecord create(UUID requestId, Long scenarioId, Instant submittedAt) {
        return jobs.save(new OptimizationJobRecord(requestId, scenarioId, submittedAt));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean recordResult(OptimizationAcknowledgment result) {
        if (result == null || result.requestId() == null || result.scenarioId() == null || result.acknowledgedAt() == null) return false;
        var job = jobs.findById(result.requestId()).orElse(null);
        if (job == null || !Objects.equals(job.getScenarioId(), result.scenarioId())) return false;
        job.complete(result.status(), result.acknowledgedAt(), result.routes());
        jobs.save(job);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID requestId) {
        jobs.findById(requestId).ifPresent(job -> {
            job.fail(Instant.now());
            jobs.save(job);
        });
    }

    @Transactional(readOnly = true)
    public OptimizationJobRecord get(UUID requestId) {
        return jobs.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Optimization job", requestId));
    }

    @Transactional(readOnly = true)
    public OptimizationJobRecord getCompletedJob(UUID requestId) {
        var job = get(requestId);
        if ("SUBMITTED".equals(job.getStatus())) {
            throw new OperationConflictException("Optimization is still running; poll the job status and retry.");
        }
        if ("FAILED".equals(job.getStatus())) {
            throw new OperationConflictException("Optimization job failed before the Worker returned a result.");
        }
        return job;
    }
}
