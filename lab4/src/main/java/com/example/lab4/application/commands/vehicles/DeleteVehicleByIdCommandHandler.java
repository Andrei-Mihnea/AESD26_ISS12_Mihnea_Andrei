package com.example.lab4.application.commands.vehicles;

import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteVehicleByIdCommandHandler implements CommandHandler<DeleteVehicleByIdCommand, Void> {
    private final VehicleRepository vehicleRepository;

    public DeleteVehicleByIdCommandHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<DeleteVehicleByIdCommand> messageType() { return DeleteVehicleByIdCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteVehicleByIdCommand command) {
        Long id = command.id();
        vehicleRepository.deleteById(id);
        return null;
    }

}
