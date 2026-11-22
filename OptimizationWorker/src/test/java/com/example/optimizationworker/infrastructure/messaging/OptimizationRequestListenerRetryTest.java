package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.commands.AcceptOptimizationRequestCommand;
import com.example.optimizationworker.application.commands.AcceptOptimizationRequestCommandHandler;
import com.example.vros.messaging.OptimizationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
class OptimizationRequestListenerRetryTest {
    private final OptimizationRequest request = new OptimizationRequest(UUID.randomUUID(), 7L, "retry-test", Instant.now(), List.of(), List.of());

    @Test
    void retriesTransientFailuresAndAcknowledgesAfterSuccessfulAttempt() {
        var handler = new StubHandler(2, false);

        new OptimizationRequestListener(handler, 3, 0).receive(request);

        org.junit.jupiter.api.Assertions.assertEquals(3, handler.calls);
    }

    @Test
    void rejectsAfterConfiguredAttemptsSoRabbitCanDeadLetterTheMessage() {
        var handler = new StubHandler(Integer.MAX_VALUE, false);

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> new OptimizationRequestListener(handler, 3, 0).receive(request));

        org.junit.jupiter.api.Assertions.assertEquals(3, handler.calls);
    }

    @Test
    void doesNotRetryInvalidMessages() {
        var handler = new StubHandler(0, true);

        assertThrows(AmqpRejectAndDontRequeueException.class,
                () -> new OptimizationRequestListener(handler, 3, 0).receive(request));

        org.junit.jupiter.api.Assertions.assertEquals(1, handler.calls);
    }

    private static final class StubHandler extends AcceptOptimizationRequestCommandHandler {
        private final int failuresBeforeSuccess;
        private final boolean invalid;
        private int calls;

        private StubHandler(int failuresBeforeSuccess, boolean invalid) {
            super(acknowledgment -> {}, new com.example.optimizationworker.application.commands.GreedyVrpSolver());
            this.failuresBeforeSuccess = failuresBeforeSuccess;
            this.invalid = invalid;
        }

        @Override public void handle(AcceptOptimizationRequestCommand command) {
            calls++;
            if (invalid) throw new IllegalArgumentException("invalid request");
            if (calls <= failuresBeforeSuccess) throw new IllegalStateException("temporary processing failure");
        }
    }
}
