package com.example.lab4.api.controllers;

import com.example.lab4.application.commands.customers.CreateCustomerCommand;
import com.example.lab4.application.commands.customers.DeleteCustomerCommand;
import com.example.lab4.application.commands.customers.UpdateCustomerCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.application.queries.customers.FindCustomerByIdQuery;
import com.example.lab4.application.queries.customers.GetCustomersQuery;
import com.example.lab4.application.queries.deliveries.GetCustomerDeliveriesQuery;
import com.example.lab4.api.mapping.CustomerRepresentationAssembler;
import com.example.lab4.application.exceptions.ResourceNotFoundException;

import com.example.lab4.application.dto.DeliveryResponse;

import com.example.lab4.application.dto.CustomerRequest;
import com.example.lab4.application.dto.CustomerResponse;

import com.example.lab4.domain.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/customers", "/api/v2/customers", "/api/customers"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@Tag(name = "Customers", description = "Create, read, update and delete customers")
@PreAuthorize("isAuthenticated()")
public class CustomerController {
    private final QueryBus queries;
    private final CommandBus commands;

    private final CustomerRepresentationAssembler representations;

    public CustomerController(QueryBus queries, CommandBus commands,
                              CustomerRepresentationAssembler representations) {
        this.queries = queries;
        this.commands = commands;
        this.representations = representations;
    }

    @GetMapping
    @Operation(summary = "List all customers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerResponse> findAll() {
        return queries.execute(new GetCustomersQuery()).stream().map(representations::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a customer by ID", description = "Returns an existing customer's details and self, customers, and deliveries links. "
            + "Select application/json or application/xml using Accept. Copy the ETag response header "
            + "into If-None-Match on a subsequent request to receive 304 when the representation is unchanged.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerResponse.class)),
                    @Content(mediaType = MediaType.APPLICATION_XML_VALUE,
                            schema = @Schema(implementation = CustomerResponse.class))}),
            @ApiResponse(responseCode = "400", description = "The customer ID is not a valid integer", content = @Content),
            @ApiResponse(responseCode = "404", description = "No customer exists with this ID",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CustomerResponse findById(
            @Parameter(description = "ID of an existing customer; the startup demo normally has ID 1", example = "1")
            @PathVariable Long id) {
        return queries.execute(new FindCustomerByIdQuery(id)).map(representations::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    @GetMapping("/{id}/deliveries")
    @Operation(summary = "List a customer's deliveries", description = "Follow the deliveries link in a customer "
            + "response to discover this endpoint. Returns an empty list when the customer has no deliveries, "
            + "or 404 when the customer does not exist. Supports JSON, XML, and conditional GET with ETag.")
    public List<DeliveryResponse> findDeliveries(@PathVariable Long id) {
        return queries.execute(new GetCustomerDeliveriesQuery(id));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Create a customer")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        User customer = commands.execute(new CreateCustomerCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(location).body(representations.toResponse(customer));
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Replace a customer's details")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return commands.execute(new UpdateCustomerCommand(id, request.name(), request.email(), request.phone(),
                        request.address(), request.coordinates()))
                .map(representations::toResponse).orElseThrow(() -> notFound(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer with no deliveries")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        if (!commands.execute(new DeleteCustomerCommand(id))) {
            throw notFound(id);
        }
    }

    private ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Customer", id);
    }
}
