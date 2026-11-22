package com.example.lab4.application.queries.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindVehicleByIdQueryHandler implements QueryHandler<FindVehicleByIdQuery, Optional<Vehicle>> {
    private final VehicleRepository vehicleRepository;

    public FindVehicleByIdQueryHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<FindVehicleByIdQuery> messageType() { return FindVehicleByIdQuery.class; }

    @Override
    public Optional<Vehicle> handle(FindVehicleByIdQuery query) {
        Long id = query.id();
        return vehicleRepository.findById(id);
    }

}
