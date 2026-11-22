package com.example.lab4.application.commands.vehicles;

import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveVehiclesCommandHandler implements CommandHandler<SaveVehiclesCommand, List<Vehicle>> {
    private final VehicleRepository vehicleRepository;

    public SaveVehiclesCommandHandler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override public Class<SaveVehiclesCommand> messageType() { return SaveVehiclesCommand.class; }

    @Transactional
    @Override
    public List<Vehicle> handle(SaveVehiclesCommand command) {
        Iterable<Vehicle> vehicles = command.vehicles();
        return vehicleRepository.saveAll(vehicles);
    }

}
