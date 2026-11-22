package com.example.lab4.application.queries.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record FindVehicleByNameQuery(String name) implements Query<Optional<Vehicle>> {}
