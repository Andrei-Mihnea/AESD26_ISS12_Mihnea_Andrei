package com.example.lab4.application.commands.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.cqrs.Command;

public record SaveVehicleCommand(Vehicle vehicle) implements Command<Vehicle> {}
