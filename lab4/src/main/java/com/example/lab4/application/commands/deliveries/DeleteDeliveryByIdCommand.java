package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.cqrs.Command;

public record DeleteDeliveryByIdCommand(Long id) implements Command<Void> {}
