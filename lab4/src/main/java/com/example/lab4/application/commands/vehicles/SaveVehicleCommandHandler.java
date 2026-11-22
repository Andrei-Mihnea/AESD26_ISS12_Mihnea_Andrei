package com.example.lab4.application.commands.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveVehicleCommandHandler implements CommandHandler<SaveVehicleCommand, Vehicle> {
    private final VehicleRepository vehicleRepository;

    public SaveVehicleCommandHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<SaveVehicleCommand> messageType() { return SaveVehicleCommand.class; }

    @Transactional
    @Override
    public Vehicle handle(SaveVehicleCommand command) {
        Vehicle vehicle = command.vehicle();
        return vehicleRepository.save(vehicle);
    }

}
