package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.cqrs.Command;

public record DeleteScenarioByIdCommand(Long id) implements Command<Void> {}
