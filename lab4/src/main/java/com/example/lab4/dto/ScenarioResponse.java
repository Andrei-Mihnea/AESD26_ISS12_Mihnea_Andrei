package com.example.lab4.dto;

import com.example.lab4.entity.Scenario;
import com.example.lab4.entity.Vehicle;
import com.example.lab4.entity.Delivery;
import java.util.Set;
import java.util.stream.Collectors;

public record ScenarioResponse(Long id, String name, Set<Long> vehicleIds, Set<Long> deliveryIds) {
    public static ScenarioResponse from(Scenario scenario) {
        return new ScenarioResponse(scenario.getId(), scenario.getName(),
                scenario.getVehicles().stream().map(Vehicle::getId).collect(Collectors.toSet()),
                scenario.getDeliveries().stream().map(Delivery::getId).collect(Collectors.toSet()));
    }
}
