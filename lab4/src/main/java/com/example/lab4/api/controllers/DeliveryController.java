package com.example.lab4.api.controllers;

import com.example.lab4.application.commands.deliveries.CreateDeliveryCommand;
import com.example.lab4.application.commands.deliveries.DeleteDeliveryCommand;
import com.example.lab4.application.commands.deliveries.UpdateDeliveryCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.application.queries.deliveries.GetDeliveriesQuery;
import com.example.lab4.application.queries.deliveries.GetDeliveryByIdQuery;
import com.example.lab4.application.dto.DeliveryRequest;
import com.example.lab4.application.dto.DeliveryResponse;

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
@RequestMapping({"/api/v1/deliveries", "/api/v2/deliveries", "/api/deliveries"})
@Tag(name = "Deliveries")
@PreAuthorize("isAuthenticated()")
public class DeliveryController {
    private final QueryBus queries;
    private final CommandBus commands;

    public DeliveryController(QueryBus queries, CommandBus commands) {
        this.queries = queries;
        this.commands = commands;
    }

    @GetMapping
    @Operation(summary = "List all deliveries")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryResponse> findAll() { return queries.execute(new GetDeliveriesQuery()); }

    @GetMapping("/{id}")
    @Operation(summary = "Find a delivery by ID")
    public DeliveryResponse findById(@PathVariable Long id) { return queries.execute(new GetDeliveryByIdQuery(id)); }

    @PostMapping
    @Operation(summary = "Create a delivery")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<DeliveryResponse> create(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse response = commands.execute(new CreateDeliveryCommand(request));
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a delivery's details")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public DeliveryResponse update(@PathVariable Long id, @Valid @RequestBody DeliveryRequest request) {
        return commands.execute(new UpdateDeliveryCommand(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a delivery")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { commands.execute(new DeleteDeliveryCommand(id)); }
}
