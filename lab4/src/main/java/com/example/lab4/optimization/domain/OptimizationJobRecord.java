package com.example.lab4.optimization.domain;

import com.example.vros.messaging.OptimizationAcknowledgment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "optimization_jobs")
public class OptimizationJobRecord {
    @Id
    @Column(name = "request_id", nullable = false, updatable = false)
    private UUID requestId;

    @Column(name = "scenario_id", nullable = false, updatable = false)
    private Long scenarioId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<OptimizationAcknowledgment.VehicleRoute> routes = List.of();

    protected OptimizationJobRecord() {}

    public OptimizationJobRecord(UUID requestId, Long scenarioId, Instant submittedAt) {
        this.requestId = requestId;
        this.scenarioId = scenarioId;
        this.submittedAt = submittedAt;
        this.status = "SUBMITTED";
    }

    public void complete(String status, Instant completedAt, List<OptimizationAcknowledgment.VehicleRoute> routes) {
        if (!"SOLVED".equals(status) && !"UNROUTABLE".equals(status) && !"FAILED".equals(status)) {
            throw new IllegalArgumentException("Unsupported optimization result status");
        }
        // Acknowledgment delivery is at least once, so applying the same result is idempotent.
        if (this.completedAt != null) return;
        this.status = status;
        this.completedAt = completedAt;
        this.routes = routes == null ? List.of() : List.copyOf(routes);
    }

    public void fail(Instant completedAt) {
        if (this.completedAt != null) return;
        this.status = "FAILED";
        this.completedAt = completedAt;
    }

    public UUID getRequestId() { return requestId; }
    public Long getScenarioId() { return scenarioId; }
    public String getStatus() { return status; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public List<OptimizationAcknowledgment.VehicleRoute> getRoutes() { return routes; }
}
