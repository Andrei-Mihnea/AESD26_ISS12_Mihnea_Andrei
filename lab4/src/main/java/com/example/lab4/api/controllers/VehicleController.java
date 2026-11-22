package com.example.lab4.api.controllers;

import com.example.lab4.application.commands.vehicles.CreateVehicleCommand;
import com.example.lab4.application.commands.vehicles.DeleteVehicleCommand;
import com.example.lab4.application.commands.vehicles.UpdateVehicleCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.application.queries.vehicles.GetVehicleByIdQuery;
import com.example.lab4.application.queries.vehicles.GetVehiclesQuery;
import com.example.lab4.application.dto.VehicleRequest;
import com.example.lab4.application.dto.VehicleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/vehicles", "/api/v2/vehicles", "/api/vehicles"})
@Tag(name = "Vehicles")
public class VehicleController {
    private final QueryBus queries;
    private final CommandBus commands;

    public VehicleController(QueryBus queries, CommandBus commands) {
        this.queries = queries;
        this.commands = commands;
    }

    @GetMapping
    @Operation(summary = "List all vehicles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<VehicleResponse> findAll() { return queries.execute(new GetVehiclesQuery()); }

    @GetMapping("/{id}")
    @Operation(summary = "Find a vehicle by ID")
    public VehicleResponse findById(@PathVariable Long id) { return queries.execute(new GetVehicleByIdQuery(id)); }

    @PostMapping
    @Operation(summary = "Create a vehicle")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = commands.execute(new CreateVehicleCommand(request));
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a vehicle's details")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return commands.execute(new UpdateVehicleCommand(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vehicle")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { commands.execute(new DeleteVehicleCommand(id)); }
}
