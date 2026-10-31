package com.example.lab4.service;

import com.example.lab4.exception.ResourceNotFoundException;

import com.example.lab4.entity.Delivery;
import com.example.lab4.entity.Scenario;
import com.example.lab4.entity.Vehicle;
import com.example.lab4.repository.IDeliveryRepository;
import com.example.lab4.repository.ScenarioRepository;
import com.example.lab4.repository.VehicleRepository;
import com.example.lab4.dto.ScenarioRequest;
import com.example.lab4.dto.ScenarioResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final VehicleRepository vehicles;
    private final IDeliveryRepository deliveries;

    public ScenarioService(ScenarioRepository scenarioRepository, VehicleRepository vehicles, IDeliveryRepository deliveries) {
        this.scenarioRepository = scenarioRepository;
        this.vehicles = vehicles;
        this.deliveries = deliveries;
    }

    public List<Scenario> findAll() { return scenarioRepository.findAll(); }
    public Optional<Scenario> findById(Long id) { return scenarioRepository.findById(id); }

    @Transactional
    public Scenario save(Scenario scenario) { return scenarioRepository.save(scenario); }

    @Transactional
    public void deleteById(Long id) { scenarioRepository.deleteById(id); }

    private Scenario require(Long id) {
        return scenarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Scenario", id));
    }

    public List<ScenarioResponse> findAllResponses() {
        return scenarioRepository.findAll().stream().map(ScenarioResponse::from).toList();
    }

    public ScenarioResponse findResponse(Long id) { return ScenarioResponse.from(require(id)); }

    @Transactional
    public void deleteExisting(Long id) {
        scenarioRepository.delete(require(id));
        scenarioRepository.flush();
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

    @Transactional
    public ScenarioResponse create(ScenarioRequest request) {
        Scenario scenario = new Scenario(request.name());
        apply(scenario, request);
        return ScenarioResponse.from(scenarioRepository.save(scenario));
    }

    @Transactional
    public ScenarioResponse update(Long id, ScenarioRequest request) {
        Scenario scenario = require(id);
        apply(scenario, request);
        return ScenarioResponse.from(scenario);
    }
}
