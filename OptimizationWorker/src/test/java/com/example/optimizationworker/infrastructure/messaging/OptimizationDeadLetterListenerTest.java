package com.example.optimizationworker.infrastructure.messaging;

import com.example.optimizationworker.application.interfaces.AcknowledgmentPublisher;
import com.example.vros.messaging.OptimizationAcknowledgment;
import com.example.vros.messaging.OptimizationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptimizationDeadLetterListenerTest {
    @Test
    void publishesFailedJobResultForDeadLetteredRequest() {
        var published = new ArrayList<OptimizationAcknowledgment>();
        var requestId = UUID.randomUUID();
        var request = new OptimizationRequest(requestId, 42L, "failed-job", Instant.now(), List.of(), List.of());
        var message = new Message(new byte[0], new MessageProperties());

        new OptimizationDeadLetterListener(published::add).receive(request, message);

        assertEquals(1, published.size());
        assertEquals(requestId, published.getFirst().requestId());
        assertEquals(42L, published.getFirst().scenarioId());
        assertEquals("FAILED", published.getFirst().status());
        assertEquals(List.of(), published.getFirst().routes());
    }
}
