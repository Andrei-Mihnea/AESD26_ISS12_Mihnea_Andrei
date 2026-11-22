package com.example.lab4.application.queries.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class ListVehicleEntitiesQueryHandler implements QueryHandler<ListVehicleEntitiesQuery, List<Vehicle>> {
    private final VehicleRepository vehicleRepository;

    public ListVehicleEntitiesQueryHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<ListVehicleEntitiesQuery> messageType() { return ListVehicleEntitiesQuery.class; }

    @Override
    public List<Vehicle> handle(ListVehicleEntitiesQuery query) {

        return vehicleRepository.findAll();
    }

}
