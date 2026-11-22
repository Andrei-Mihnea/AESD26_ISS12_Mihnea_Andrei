package com.example.lab4.application.commands.customers;

import com.example.lab4.application.cqrs.Command;

public record DeleteCustomerCommand(Long id) implements Command<Boolean> {}
