package com.example.lab4.api.controllers;

import com.example.lab4.application.commands.scenarios.CreateScenarioCommand;
import com.example.lab4.application.commands.scenarios.DeleteScenarioCommand;
import com.example.lab4.application.commands.scenarios.UpdateScenarioCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.application.queries.scenarios.GetScenarioByIdQuery;
import com.example.lab4.application.queries.scenarios.GetScenariosQuery;
import com.example.lab4.application.dto.ScenarioRequest;
import com.example.lab4.application.dto.ScenarioResponse;

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
@RequestMapping({"/api/v1/scenarios", "/api/v2/scenarios", "/api/scenarios"})
@Tag(name = "Scenarios")
public class ScenarioController {
    private final QueryBus queries;
    private final CommandBus commands;

    public ScenarioController(QueryBus queries, CommandBus commands) {
        this.queries = queries;
        this.commands = commands;
    }

    @GetMapping
    @Operation(summary = "List all scenarios")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ScenarioResponse> findAll() { return queries.execute(new GetScenariosQuery()); }

    @GetMapping("/{id}")
    @Operation(summary = "Find a scenario by ID")
    public ScenarioResponse findById(@PathVariable Long id) { return queries.execute(new GetScenarioByIdQuery(id)); }

    @PostMapping
    @Operation(summary = "Create a scenario")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<ScenarioResponse> create(@Valid @RequestBody ScenarioRequest request) {
        ScenarioResponse response = commands.execute(new CreateScenarioCommand(request));
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a scenario's details")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ScenarioResponse update(@PathVariable Long id, @Valid @RequestBody ScenarioRequest request) {
        return commands.execute(new UpdateScenarioCommand(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a scenario")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { commands.execute(new DeleteScenarioCommand(id)); }
}
