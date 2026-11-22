package com.example.lab4.application.commands.drivers;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.cqrs.Command;

public record SaveDriverCommand(User driver) implements Command<User> {}
