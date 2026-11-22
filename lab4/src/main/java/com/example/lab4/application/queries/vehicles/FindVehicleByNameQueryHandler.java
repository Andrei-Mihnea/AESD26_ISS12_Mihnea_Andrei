package com.example.lab4.application.queries.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindVehicleByNameQueryHandler implements QueryHandler<FindVehicleByNameQuery, Optional<Vehicle>> {
    private final VehicleRepository vehicleRepository;

    public FindVehicleByNameQueryHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<FindVehicleByNameQuery> messageType() { return FindVehicleByNameQuery.class; }

    @Override
    public Optional<Vehicle> handle(FindVehicleByNameQuery query) {
        String name = query.name();
        return vehicleRepository.findByName(name);
    }

}
