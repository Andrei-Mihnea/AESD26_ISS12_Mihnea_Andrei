package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.dto.ScenarioRequest;
import com.example.lab4.application.dto.ScenarioResponse;
import com.example.lab4.application.cqrs.Command;

public record UpdateScenarioCommand(Long id, ScenarioRequest request) implements Command<ScenarioResponse> {}
