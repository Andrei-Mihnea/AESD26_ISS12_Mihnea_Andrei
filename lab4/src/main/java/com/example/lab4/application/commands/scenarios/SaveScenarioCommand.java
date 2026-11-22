package com.example.lab4.application.commands.scenarios;

import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.cqrs.Command;

public record SaveScenarioCommand(Scenario scenario) implements Command<Scenario> {}
