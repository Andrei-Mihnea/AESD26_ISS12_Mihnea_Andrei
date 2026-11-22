package com.example.lab4.application.dto;

import com.example.lab4.domain.entities.Vehicle;

public record VehicleResponse(Long id, String name, Integer capacity, boolean available) {
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(vehicle.getId(), vehicle.getName(), vehicle.getCapacity(), vehicle.isAvailable());
    }
}
