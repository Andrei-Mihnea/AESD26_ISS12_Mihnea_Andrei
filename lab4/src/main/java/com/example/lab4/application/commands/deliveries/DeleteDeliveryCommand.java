package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.cqrs.Command;

public record DeleteDeliveryCommand(Long id) implements Command<Void> {}
