package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.dto.LoginResponse;
import com.example.lab4.application.cqrs.Command;

public record RefreshAccessTokenCommand(String rawToken) implements Command<LoginResponse> {
    @Override public String toString() { return "RefreshAccessTokenCommand[REDACTED]"; }
}
