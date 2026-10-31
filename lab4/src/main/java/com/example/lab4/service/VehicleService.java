package com.example.lab4.service;

import com.example.lab4.exception.ResourceNotFoundException;

import com.example.lab4.entity.Depot;
import com.example.lab4.entity.Vehicle;
import com.example.lab4.repository.DepotRepository;
import com.example.lab4.repository.VehicleRepository;
import com.example.lab4.dto.VehicleRequest;
import com.example.lab4.dto.VehicleResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final DepotRepository depots;

    public VehicleService(VehicleRepository vehicleRepository, DepotRepository depots) {
        this.vehicleRepository = vehicleRepository;
        this.depots = depots;
    }

    public List<Vehicle> findAll() { return vehicleRepository.findAll(); }
    public Optional<Vehicle> findById(Long id) { return vehicleRepository.findById(id); }
    public Optional<Vehicle> findByName(String name) { return vehicleRepository.findByName(name); }
    public List<Vehicle> findAvailableAtDepot(Long depotId) {
        return vehicleRepository.findAvailableAtDepot(depotId);
    }

    @Transactional
    public Vehicle save(Vehicle vehicle) { return vehicleRepository.save(vehicle); }

    @Transactional
    public List<Vehicle> saveAll(Iterable<Vehicle> vehicles) { return vehicleRepository.saveAll(vehicles); }

    @Transactional
    public void deleteById(Long id) { vehicleRepository.deleteById(id); }

    @Transactional
    public int updateAvailability(Long vehicleId, boolean available) {
        return vehicleRepository.updateAvailability(vehicleId, available);
    }

    private Vehicle require(Long id) {
        return vehicleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Vehicle", id));
    }

    public List<VehicleResponse> findAllResponses() {
        return vehicleRepository.findAll().stream().map(VehicleResponse::from).toList();
    }

    public VehicleResponse findResponse(Long id) { return VehicleResponse.from(require(id)); }

    @Transactional
    public void deleteExisting(Long id) {
        vehicleRepository.delete(require(id));
        vehicleRepository.flush();
    }

    private Depot defaultDepot() {
        // Older lab4 databases may have several depots; consistently use the first one.
        return depots.findFirstByOrderByIdAsc().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.CONFLICT, "A depot must exist before creating vehicles"));
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        return VehicleResponse.from(vehicleRepository.save(new Vehicle(request.name(), request.capacity(),
                defaultDepot(), request.available(), null)));
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = require(id);
        vehicle.updateDetails(request.name(), request.capacity(), request.available(), defaultDepot());
        return VehicleResponse.from(vehicle);
    }
}
