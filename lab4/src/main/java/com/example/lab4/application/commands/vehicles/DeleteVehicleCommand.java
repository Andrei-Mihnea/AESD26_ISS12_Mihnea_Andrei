package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.cqrs.Command;

public record DeleteVehicleCommand(Long id) implements Command<Void> {}
