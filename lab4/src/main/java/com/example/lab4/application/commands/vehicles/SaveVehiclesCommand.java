package com.example.lab4.application.commands.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import java.util.List;
import com.example.lab4.application.cqrs.Command;

public record SaveVehiclesCommand(Iterable<Vehicle> vehicles) implements Command<List<Vehicle>> {}
