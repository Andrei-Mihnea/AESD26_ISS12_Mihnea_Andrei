package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.dto.CustomerRequest;
import com.example.lab4.application.cqrs.Command;

public record CreateCustomerCommand(CustomerRequest request) implements Command<User> {}
