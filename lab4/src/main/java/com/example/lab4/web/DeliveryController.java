package com.example.lab4.web;

import com.example.lab4.dto.DeliveryRequest;
import com.example.lab4.dto.DeliveryResponse;

import com.example.lab4.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries")
public class DeliveryController {
    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all deliveries")
    public List<DeliveryResponse> findAll() { return service.findAllResponses(); }

    @GetMapping("/{id}")
    @Operation(summary = "Find a delivery by ID")
    public DeliveryResponse findById(@PathVariable Long id) { return service.findResponse(id); }

    @PostMapping
    @Operation(summary = "Create a delivery")
    public ResponseEntity<DeliveryResponse> create(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse response = service.create(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a delivery's details")
    public DeliveryResponse update(@PathVariable Long id, @Valid @RequestBody DeliveryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a delivery")
    public void delete(@PathVariable Long id) { service.deleteExisting(id); }
}
