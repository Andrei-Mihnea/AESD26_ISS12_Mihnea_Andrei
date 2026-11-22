package com.example.lab4.application.queries.vehicles;

import com.example.lab4.application.dto.VehicleResponse;
import com.example.lab4.application.cqrs.Query;

public record GetVehicleByIdQuery(Long id) implements Query<VehicleResponse> {}
