package com.example.lab4.application.commands.deliveries;

import com.example.lab4.domain.entities.Delivery;
import java.util.List;
import com.example.lab4.application.cqrs.Command;

public record SaveDeliveriesCommand(Iterable<Delivery> deliveries) implements Command<List<Delivery>> {}
