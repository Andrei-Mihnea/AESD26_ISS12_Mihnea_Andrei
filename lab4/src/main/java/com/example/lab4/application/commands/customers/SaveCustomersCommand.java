package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.entities.User;
import java.util.List;
import com.example.lab4.application.cqrs.Command;

public record SaveCustomersCommand(Iterable<User> customers) implements Command<List<User>> {}
