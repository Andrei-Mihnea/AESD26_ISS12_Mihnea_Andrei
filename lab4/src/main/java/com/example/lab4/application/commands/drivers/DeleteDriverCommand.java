package com.example.lab4.application.commands.drivers;

import com.example.lab4.application.cqrs.Command;

public record DeleteDriverCommand(Long id) implements Command<Void> {}
