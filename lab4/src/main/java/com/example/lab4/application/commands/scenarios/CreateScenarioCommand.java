package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.dto.ScenarioRequest;
import com.example.lab4.application.dto.ScenarioResponse;
import com.example.lab4.application.cqrs.Command;

public record CreateScenarioCommand(ScenarioRequest request) implements Command<ScenarioResponse> {}
