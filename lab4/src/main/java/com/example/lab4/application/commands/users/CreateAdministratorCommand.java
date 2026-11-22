package com.example.lab4.application.commands.users;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.cqrs.Command;

public record CreateAdministratorCommand(String name, String email, String phone) implements Command<User> {}
