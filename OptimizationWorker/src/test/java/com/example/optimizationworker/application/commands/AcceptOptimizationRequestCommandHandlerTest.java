package com.example.optimizationworker.application.commands;

import com.example.vros.messaging.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class AcceptOptimizationRequestCommandHandlerTest {
    @Test void publishesCorrelatedRoutingSolutionThroughItsPort() {
        var published = new ArrayList<OptimizationAcknowledgment>();
        var id = UUID.randomUUID();
        new AcceptOptimizationRequestCommandHandler(published::add, new GreedyVrpSolver()).handle(new AcceptOptimizationRequestCommand(new OptimizationRequest(id, 12L, "Test", Instant.now(),
                List.of(new OptimizationRequest.VehicleInput(1L, 10, "44,26")),
                List.of(new OptimizationRequest.DeliveryInput(2L, 3, "44.1,26.1")))));
        assertEquals(1, published.size());
        assertEquals(id, published.getFirst().requestId());
        assertEquals(12L, published.getFirst().scenarioId());
        assertEquals("SOLVED", published.getFirst().status());
        assertNotNull(published.getFirst().acknowledgedAt());
        assertEquals(List.of(2L), published.getFirst().routes().getFirst().deliveryIds());
    }
    @Test void rejectsInvalidRequestsWithoutPublishing() {
        var published = new ArrayList<OptimizationAcknowledgment>();
        assertThrows(IllegalArgumentException.class, () -> new AcceptOptimizationRequestCommandHandler(published::add, new GreedyVrpSolver()).handle(
                new AcceptOptimizationRequestCommand(new OptimizationRequest(null, 12L, "Test", Instant.now(), List.of(), List.of()))));
        assertTrue(published.isEmpty());
    }
}
