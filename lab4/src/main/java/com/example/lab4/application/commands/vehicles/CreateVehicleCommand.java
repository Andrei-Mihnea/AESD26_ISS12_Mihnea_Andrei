package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.dto.VehicleRequest;
import com.example.lab4.application.dto.VehicleResponse;
import com.example.lab4.application.cqrs.Command;

public record CreateVehicleCommand(VehicleRequest request) implements Command<VehicleResponse> {}
