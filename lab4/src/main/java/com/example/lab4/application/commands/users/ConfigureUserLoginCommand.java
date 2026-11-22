package com.example.lab4.application.commands.users;

import com.example.lab4.application.cqrs.Command;

public record ConfigureUserLoginCommand(Long id, String username, String rawPassword) implements Command<Void> {
    @Override public String toString() { return "ConfigureUserLoginCommand[REDACTED]"; }
}
