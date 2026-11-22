package com.example.lab4.application.queries.vehicles;

import com.example.lab4.application.interfaces.VehicleRepository;
import com.example.lab4.application.dto.VehicleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetVehiclesQueryHandler implements QueryHandler<GetVehiclesQuery, List<VehicleResponse>> {
    private final VehicleRepository vehicleRepository;

    public GetVehiclesQueryHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<GetVehiclesQuery> messageType() { return GetVehiclesQuery.class; }

    @Override
    public List<VehicleResponse> handle(GetVehiclesQuery query) {

        return vehicleRepository.findAll().stream().map(VehicleResponse::from).toList();
    }

}
