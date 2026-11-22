package com.example.lab4.application.commands.depots;

import com.example.lab4.application.cqrs.Command;

public record DeleteDepotCommand(Long id) implements Command<Void> {}
