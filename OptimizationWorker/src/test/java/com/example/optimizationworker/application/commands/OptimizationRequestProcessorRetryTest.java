package com.example.optimizationworker.application.commands;

import com.example.vros.messaging.OptimizationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptimizationRequestProcessorRetryTest {
    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    private final OptimizationRequest request = new OptimizationRequest(UUID.randomUUID(), 7L, "retry-test", Instant.now(), List.of(), List.of());

    @AfterEach void closeExecutor() { executor.shutdown(); }

    @Test
    void retriesTransientFailuresAndCompletesAfterSuccessfulAttempt() {
        var handler = new StubHandler(2, false);
        var processor = processor(handler, 3);

        processor.processWithRetries(request);

        assertEquals(3, handler.calls);
    }

    @Test
    void rejectsAfterConfiguredAttemptsSoRabbitCanDeadLetterTheMessage() {
        var handler = new StubHandler(Integer.MAX_VALUE, false);
        var processor = processor(handler, 3);

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> processor.processWithRetries(request));

        assertEquals(3, handler.calls);
    }

    @Test
    void doesNotRetryInvalidMessages() {
        var handler = new StubHandler(0, true);
        var processor = processor(handler, 3);

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> processor.processWithRetries(request));

        assertEquals(1, handler.calls);
    }

    private OptimizationRequestProcessor processor(StubHandler handler, int attempts) {
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(0);
        executor.initialize();
        return new OptimizationRequestProcessor(handler, new OptimizationWorkerMetrics(executor), attempts, 0);
    }

    private static final class StubHandler extends AcceptOptimizationRequestCommandHandler {
        private final int failuresBeforeSuccess;
        private final boolean invalid;
        private int calls;

        private StubHandler(int failuresBeforeSuccess, boolean invalid) {
            super(ack -> {}, new GreedyVrpSolver());
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
