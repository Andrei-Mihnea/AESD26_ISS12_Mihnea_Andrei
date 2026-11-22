package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.cqrs.Command;

public record DeleteScenarioCommand(Long id) implements Command<Void> {}
