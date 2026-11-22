package com.example.lab4.application.commands.users;

import com.example.lab4.application.cqrs.Command;

public record DisableUserLoginCommand(Long id) implements Command<Void> {}
