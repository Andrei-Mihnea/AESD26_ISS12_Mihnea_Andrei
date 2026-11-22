package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.dto.RegistrationRequest;
import com.example.lab4.application.dto.RegistrationResponse;
import jakarta.validation.Valid;
import com.example.lab4.application.cqrs.Command;

public record RegisterUserCommand(@Valid RegistrationRequest request) implements Command<RegistrationResponse> {
    @Override public String toString() { return "RegisterUserCommand[REDACTED]"; }
}
