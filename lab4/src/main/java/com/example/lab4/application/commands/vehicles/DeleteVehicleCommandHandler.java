package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteVehicleCommandHandler implements CommandHandler<DeleteVehicleCommand, Void> {
    private final VehicleRepository vehicleRepository;

    public DeleteVehicleCommandHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<DeleteVehicleCommand> messageType() { return DeleteVehicleCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteVehicleCommand command) {
        Long id = command.id();
        vehicleRepository.delete(require(id));
        vehicleRepository.flush();

        return null;
    }

    private Vehicle require(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vehicle", id));
    }
}
