package com.example.lab4.application.commands.customers;

import com.example.lab4.application.cqrs.Command;

public record DeleteCustomerByIdCommand(Long id) implements Command<Void> {}
