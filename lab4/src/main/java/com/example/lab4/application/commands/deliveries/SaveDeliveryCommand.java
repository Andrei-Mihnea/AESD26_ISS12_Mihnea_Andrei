package com.example.lab4.application.commands.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.cqrs.Command;

public record SaveDeliveryCommand(Delivery delivery) implements Command<Delivery> {}
