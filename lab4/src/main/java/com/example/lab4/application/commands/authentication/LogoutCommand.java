package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.cqrs.Command;

public record LogoutCommand(String rawToken) implements Command<Void> {
    @Override public String toString() { return "LogoutCommand[REDACTED]"; }
}
