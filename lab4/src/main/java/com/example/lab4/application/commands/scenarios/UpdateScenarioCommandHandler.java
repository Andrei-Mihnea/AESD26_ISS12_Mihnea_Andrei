package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.domain.entities.Vehicle;
import com.example.lab4.application.interfaces.DeliveryRepository;
import com.example.lab4.application.interfaces.ScenarioRepository;
import com.example.lab4.application.interfaces.VehicleRepository;
import com.example.lab4.application.dto.ScenarioRequest;
import com.example.lab4.application.dto.ScenarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class UpdateScenarioCommandHandler implements CommandHandler<UpdateScenarioCommand, ScenarioResponse> {
    private final ScenarioRepository scenarioRepository;
    private final VehicleRepository vehicles;
    private final DeliveryRepository deliveries;

    public UpdateScenarioCommandHandler(ScenarioRepository scenarioRepository, VehicleRepository vehicles, DeliveryRepository deliveries) {
        this.scenarioRepository = scenarioRepository;
        this.vehicles = vehicles;
        this.deliveries = deliveries;
    }

    @Override public Class<UpdateScenarioCommand> messageType() { return UpdateScenarioCommand.class; }

    @Transactional
    @Override
    public ScenarioResponse handle(UpdateScenarioCommand command) {
        Long id = command.id();
        ScenarioRequest request = command.request();
        Scenario scenario = require(id);
        apply(scenario, request);
        return ScenarioResponse.from(scenario);
    }

    private Scenario require(Long id) {
        return scenarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Scenario", id));
    }

    private void apply(Scenario scenario, ScenarioRequest request) {
        // Resolve every reference before changing the managed scenario.
        Set<Vehicle> selectedVehicles = new HashSet<>();
        for (Long id : request.vehicleIds()) {
            selectedVehicles.add(vehicles.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("Vehicle", id)));
        }
        Set<Delivery> selectedDeliveries = new HashSet<>();
        for (Long id : request.deliveryIds()) {
            selectedDeliveries.add(deliveries.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("Delivery", id)));
        }
        scenario.setName(request.name());
        scenario.replaceMembers(selectedVehicles, selectedDeliveries);
    }
}
