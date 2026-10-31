package com.example.lab4.web;

import com.example.lab4.exception.ResourceNotFoundException;

import com.example.lab4.dto.DeliveryResponse;
import com.example.lab4.service.DeliveryService;
import com.example.lab4.dto.CustomerRequest;
import com.example.lab4.dto.CustomerResponse;

import com.example.lab4.entity.Customer;
import com.example.lab4.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
public class CustomerController {
    private final CustomerService customers;

    private final DeliveryService deliveries;
    private final CustomerRepresentationAssembler representations;

    public CustomerController(CustomerService customers, DeliveryService deliveries,
                              CustomerRepresentationAssembler representations) {
        this.customers = customers;
        this.deliveries = deliveries;
        this.representations = representations;
    }

    @GetMapping
    @Operation(summary = "List all customers")
    public List<CustomerResponse> findAll() {
        return customers.findAll().stream().map(representations::toResponse).toList();
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
        return customers.findById(id).map(representations::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    @GetMapping("/{id}/deliveries")
    @Operation(summary = "List a customer's deliveries", description = "Follow the deliveries link in a customer "
            + "response to discover this endpoint. Returns an empty list when the customer has no deliveries, "
            + "or 404 when the customer does not exist. Supports JSON, XML, and conditional GET with ETag.")
    public List<DeliveryResponse> findDeliveries(@PathVariable Long id) {
        return deliveries.findForCustomer(id);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Create a customer")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        Customer customer = customers.save(new Customer(request.name(), request.email(), request.phone(),
                request.address(), request.coordinates()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(location).body(representations.toResponse(customer));
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Replace a customer's details")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customers.update(id, request.name(), request.email(), request.phone(),
                        request.address(), request.coordinates())
                .map(representations::toResponse).orElseThrow(() -> notFound(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer with no deliveries")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!customers.deleteIfPresent(id)) {
            throw notFound(id);
        }
    }

    private ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Customer", id);
    }
}
