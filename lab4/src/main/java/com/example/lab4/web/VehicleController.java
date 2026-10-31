package com.example.lab4.web;

import com.example.lab4.dto.VehicleRequest;
import com.example.lab4.dto.VehicleResponse;

import com.example.lab4.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/vehicles", "/api/v2/vehicles", "/api/vehicles"})
@Tag(name = "Vehicles")
public class VehicleController {
    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all vehicles")
    public List<VehicleResponse> findAll() { return service.findAllResponses(); }

    @GetMapping("/{id}")
    @Operation(summary = "Find a vehicle by ID")
    public VehicleResponse findById(@PathVariable Long id) { return service.findResponse(id); }

    @PostMapping
    @Operation(summary = "Create a vehicle")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = service.create(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a vehicle's details")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vehicle")
    public void delete(@PathVariable Long id) { service.deleteExisting(id); }
}
