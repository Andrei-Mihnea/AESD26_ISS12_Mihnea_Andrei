package com.example.lab4.application.services;

import com.example.lab4.application.exceptions.OperationConflictException;
import com.example.lab4.application.interfaces.OptimizationJobRepository;
import com.example.lab4.domain.entities.OptimizationJobRecord;
import com.example.vros.messaging.OptimizationAcknowledgment;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OptimizationJobServiceTest {
    @Test
    void storesWorkerResultAndMakesSolutionAvailableByRequestId() {
        var store = new InMemoryOptimizationJobs();
        var service = new OptimizationJobService(store);
        var requestId = UUID.randomUUID();
        var submitted = Instant.now();
        service.create(requestId, 5L, submitted);
        var route = new OptimizationAcknowledgment.VehicleRoute(9L, List.of(11L, 12L), 7, 18.4);

        assertTrue(service.recordResult(new OptimizationAcknowledgment(requestId, 5L, "SOLVED", submitted.plusSeconds(2), List.of(route))));

        var job = service.getCompletedJob(requestId);
        assertEquals("SOLVED", job.getStatus());
        assertEquals(List.of(route), job.getRoutes());
        assertEquals(submitted.plusSeconds(2), job.getCompletedAt());
    }

    @Test
    void keepsPendingJobPollableAndPreventsPrematureSolutionRead() {
        var service = new OptimizationJobService(new InMemoryOptimizationJobs());
        var requestId = UUID.randomUUID();
        service.create(requestId, 5L, Instant.now());

        assertEquals("SUBMITTED", service.get(requestId).getStatus());
        assertThrows(OperationConflictException.class, () -> service.getCompletedJob(requestId));
    }

    private static final class InMemoryOptimizationJobs implements OptimizationJobRepository {
        private final Map<UUID, OptimizationJobRecord> records = new HashMap<>();
        @Override public OptimizationJobRecord save(OptimizationJobRecord job) {
            records.put(job.getRequestId(), job);
            return job;
        }
        @Override public Optional<OptimizationJobRecord> findById(UUID requestId) {
            return Optional.ofNullable(records.get(requestId));
        }
    }
}
