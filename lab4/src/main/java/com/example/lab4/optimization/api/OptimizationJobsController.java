package com.example.lab4.optimization.api;

import com.example.lab4.optimization.application.SubmitScenarioOptimizationCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.optimization.api.OptimizationJobStatusResponse;
import com.example.lab4.optimization.api.OptimizationJobSubmissionRequest;
import com.example.lab4.optimization.api.OptimizationSolutionResponse;
import com.example.lab4.optimization.application.OptimizationJobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/optimization-jobs", "/api/v1/optimization-jobs", "/api/v2/optimization-jobs"})
@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
public class OptimizationJobsController {
    private final CommandBus commands;
    private final OptimizationJobService jobs;

    public OptimizationJobsController(CommandBus commands, OptimizationJobService jobs) {
        this.commands = commands;
        this.jobs = jobs;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OptimizationSubmission submit(
            @Valid @RequestBody OptimizationJobSubmissionRequest request) {
        return commands.execute(new SubmitScenarioOptimizationCommand(request.scenarioId()));
    }

    @GetMapping("/{requestId}/status")
    public OptimizationJobStatusResponse status(@PathVariable UUID requestId) {
        var job = jobs.get(requestId);
        return new OptimizationJobStatusResponse(job.getRequestId(), job.getScenarioId(), job.getStatus(),
                job.getSubmittedAt(), job.getCompletedAt());
    }

    @GetMapping("/{requestId}/solution")
    public OptimizationSolutionResponse solution(@PathVariable UUID requestId) {
        var job = jobs.getCompletedJob(requestId);
        return new OptimizationSolutionResponse(job.getRequestId(), job.getScenarioId(), job.getStatus(),
                job.getCompletedAt(), job.getRoutes());
    }
}
