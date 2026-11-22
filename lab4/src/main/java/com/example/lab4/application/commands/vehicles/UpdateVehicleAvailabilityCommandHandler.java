package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class UpdateVehicleAvailabilityCommandHandler implements CommandHandler<UpdateVehicleAvailabilityCommand, Integer> {
    private final VehicleRepository vehicleRepository;

    public UpdateVehicleAvailabilityCommandHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<UpdateVehicleAvailabilityCommand> messageType() { return UpdateVehicleAvailabilityCommand.class; }

    @Transactional
    @Override
    public Integer handle(UpdateVehicleAvailabilityCommand command) {
        Long vehicleId = command.vehicleId();
        boolean available = command.available();
        return vehicleRepository.updateAvailability(vehicleId, available);
    }

}
