package com.example.lab4.web;

import com.example.lab4.dto.ScenarioRequest;
import com.example.lab4.dto.ScenarioResponse;

import com.example.lab4.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/scenarios")
@Tag(name = "Scenarios")
public class ScenarioController {
    private final ScenarioService service;

    public ScenarioController(ScenarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all scenarios")
    public List<ScenarioResponse> findAll() { return service.findAllResponses(); }

    @GetMapping("/{id}")
    @Operation(summary = "Find a scenario by ID")
    public ScenarioResponse findById(@PathVariable Long id) { return service.findResponse(id); }

    @PostMapping
    @Operation(summary = "Create a scenario")
    public ResponseEntity<ScenarioResponse> create(@Valid @RequestBody ScenarioRequest request) {
        ScenarioResponse response = service.create(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a scenario's details")
    public ScenarioResponse update(@PathVariable Long id, @Valid @RequestBody ScenarioRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a scenario")
    public void delete(@PathVariable Long id) { service.deleteExisting(id); }
}
