package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.cqrs.Command;

public record DeleteVehicleByIdCommand(Long id) implements Command<Void> {}
