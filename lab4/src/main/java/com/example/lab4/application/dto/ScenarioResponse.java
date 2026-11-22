package com.example.lab4.application.dto;

import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.domain.entities.Delivery;
import java.util.Set;
import java.util.stream.Collectors;

public record ScenarioResponse(Long id, String name, Set<Long> vehicleIds, Set<Long> deliveryIds) {
    public static ScenarioResponse from(Scenario scenario) {
        return new ScenarioResponse(scenario.getId(), scenario.getName(),
                scenario.getVehicles().stream().map(Vehicle::getId).collect(Collectors.toSet()),
                scenario.getDeliveries().stream().map(Delivery::getId).collect(Collectors.toSet()));
    }
}
