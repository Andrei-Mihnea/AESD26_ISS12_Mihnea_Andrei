package com.example.lab4.application.queries.vehicles;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import com.example.lab4.application.dto.VehicleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetVehicleByIdQueryHandler implements QueryHandler<GetVehicleByIdQuery, VehicleResponse> {
    private final VehicleRepository vehicleRepository;

    public GetVehicleByIdQueryHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<GetVehicleByIdQuery> messageType() { return GetVehicleByIdQuery.class; }

    @Override
    public VehicleResponse handle(GetVehicleByIdQuery query) {
        Long id = query.id();
        return VehicleResponse.from(require(id));
    }

    private Vehicle require(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vehicle", id));
    }
}
