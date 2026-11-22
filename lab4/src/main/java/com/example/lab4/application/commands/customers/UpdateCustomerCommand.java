package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.entities.User;
import java.util.Optional;
import com.example.lab4.application.cqrs.Command;

public record UpdateCustomerCommand(Long id, String name, String email, String phone, String address, String coordinates) implements Command<Optional<User>> {}
