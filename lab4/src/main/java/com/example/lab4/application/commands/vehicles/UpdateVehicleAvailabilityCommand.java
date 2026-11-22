package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.cqrs.Command;

public record UpdateVehicleAvailabilityCommand(Long vehicleId, boolean available) implements Command<Integer> {}
