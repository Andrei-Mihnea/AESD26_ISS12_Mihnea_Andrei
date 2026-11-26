package com.example.lab4.optimization.application;

import com.example.lab4.domain.entities.*;
import com.example.lab4.domain.valueobjects.*;
import com.example.lab4.domain.enums.*;
import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.application.interfaces.ScenarioRepository;
import com.example.vros.messaging.*;
import com.example.lab4.optimization.application.ports.OptimizationRequestPublisher;
import com.example.lab4.optimization.application.OptimizationJobService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.OffsetDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmitScenarioOptimizationCommandHandlerTest {
    @Test void missingScenarioDoesNotPublish() {
        var repository = mock(ScenarioRepository.class);
        var publisher = mock(OptimizationRequestPublisher.class);
        var jobs = mock(OptimizationJobService.class);
        assertThrows(ResourceNotFoundException.class, () -> new SubmitScenarioOptimizationCommandHandler(repository, publisher, jobs).handle(new SubmitScenarioOptimizationCommand(999L)));
        verifyNoInteractions(publisher);
        verifyNoInteractions(jobs);
    }
    @Test void publishesScenarioSnapshotAndReturnsSameRequestIdAfterConfirmation() {
        var repository = mock(ScenarioRepository.class);
        var publisher = mock(OptimizationRequestPublisher.class);
        var jobs = mock(OptimizationJobService.class);
        var depot = new Depot("Depot", "44,26");
        var vehicle = new Vehicle("Van", 10, depot, true, null);
        ReflectionTestUtils.setField(vehicle, "id", 2L);
        var customer = User.customer("Customer", "test@example.com", "123", "Address", "45,27");
        var delivery = new Delivery(customer, 3, "Test", OffsetDateTime.now());
        ReflectionTestUtils.setField(delivery, "id", 3L);
        var scenario = new Scenario("Test scenario");ReflectionTestUtils.setField(scenario, "id", 1L);
        scenario.addVehicle(vehicle);scenario.addDelivery(delivery);
        when(repository.findById(1L)).thenReturn(Optional.of(scenario));
        var result = new SubmitScenarioOptimizationCommandHandler(repository, publisher, jobs).handle(new SubmitScenarioOptimizationCommand(1L));
        var captured = ArgumentCaptor.forClass(OptimizationRequest.class);
        verify(publisher).publish(captured.capture());
        verify(jobs).create(eq(result.requestId()), eq(1L), any());
        assertEquals(result.requestId(), captured.getValue().requestId());
        assertEquals("SUBMITTED", result.status());
        assertEquals("Test scenario", captured.getValue().scenarioName());
        assertEquals(10, captured.getValue().vehicles().getFirst().capacity());
        assertEquals("44,26", captured.getValue().vehicles().getFirst().depotCoordinates());
        assertEquals(3, captured.getValue().deliveries().getFirst().demand());
        assertEquals("45,27", captured.getValue().deliveries().getFirst().coordinates());
    }
}
