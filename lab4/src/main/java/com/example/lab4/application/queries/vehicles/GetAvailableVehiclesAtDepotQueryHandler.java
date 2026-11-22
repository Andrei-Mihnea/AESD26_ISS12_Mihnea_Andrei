package com.example.lab4.application.queries.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetAvailableVehiclesAtDepotQueryHandler implements QueryHandler<GetAvailableVehiclesAtDepotQuery, List<Vehicle>> {
    private final VehicleRepository vehicleRepository;

    public GetAvailableVehiclesAtDepotQueryHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<GetAvailableVehiclesAtDepotQuery> messageType() { return GetAvailableVehiclesAtDepotQuery.class; }

    @Override
    public List<Vehicle> handle(GetAvailableVehiclesAtDepotQuery query) {
        Long depotId = query.depotId();
        return vehicleRepository.findAvailableAtDepot(depotId);
    }

}
