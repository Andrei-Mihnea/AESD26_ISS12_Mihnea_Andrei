package com.example.lab4.application.commands.drivers;

import com.example.lab4.domain.entities.User;
import java.util.List;
import com.example.lab4.application.cqrs.Command;

public record SaveDriversCommand(Iterable<User> drivers) implements Command<List<User>> {}
