package com.example.lab4.application.queries.vehicles;

import com.example.lab4.application.dto.VehicleResponse;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetVehiclesQuery() implements Query<List<VehicleResponse>> {}
