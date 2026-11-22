package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Depot;
import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.DepotRepository;
import com.example.lab4.application.interfaces.VehicleRepository;
import com.example.lab4.application.dto.VehicleRequest;
import com.example.lab4.application.dto.VehicleResponse;
import com.example.lab4.application.exceptions.OperationConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class UpdateVehicleCommandHandler implements CommandHandler<UpdateVehicleCommand, VehicleResponse> {
    private final VehicleRepository vehicleRepository;
    private final DepotRepository depots;

    public UpdateVehicleCommandHandler(VehicleRepository vehicleRepository, DepotRepository depots) {
        this.vehicleRepository = vehicleRepository;
        this.depots = depots;
    }

    @Override public Class<UpdateVehicleCommand> messageType() { return UpdateVehicleCommand.class; }

    @Transactional
    @Override
    public VehicleResponse handle(UpdateVehicleCommand command) {
        Long id = command.id();
        VehicleRequest request = command.request();
        Vehicle vehicle = require(id);
        vehicle.updateDetails(request.name(), request.capacity(), request.available(), defaultDepot());
        return VehicleResponse.from(vehicle);
    }

    private Vehicle require(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vehicle", id));
    }

    private Depot defaultDepot() {
        // Older lab4 databases may have several depots; consistently use the first one.
        return depots.findFirstByOrderByIdAsc().orElseThrow(() ->
                new OperationConflictException("A depot must exist before creating vehicles"));
    }
}
