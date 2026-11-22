package com.example.lab4.application.commands.vehicles;

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
public class CreateVehicleCommandHandler implements CommandHandler<CreateVehicleCommand, VehicleResponse> {
    private final VehicleRepository vehicleRepository;
    private final DepotRepository depots;

    public CreateVehicleCommandHandler(VehicleRepository vehicleRepository, DepotRepository depots) {
        this.vehicleRepository = vehicleRepository;
        this.depots = depots;
    }

    @Override public Class<CreateVehicleCommand> messageType() { return CreateVehicleCommand.class; }

    @Transactional
    @Override
    public VehicleResponse handle(CreateVehicleCommand command) {
        VehicleRequest request = command.request();
        return VehicleResponse.from(vehicleRepository.save(new Vehicle(request.name(), request.capacity(),
                defaultDepot(), request.available(), null)));
    }

    private Depot defaultDepot() {
        // Older lab4 databases may have several depots; consistently use the first one.
        return depots.findFirstByOrderByIdAsc().orElseThrow(() ->
                new OperationConflictException("A depot must exist before creating vehicles"));
    }
}
