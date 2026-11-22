package com.example.lab4.application.queries.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetAvailableVehiclesAtDepotQuery(Long depotId) implements Query<List<Vehicle>> {}
