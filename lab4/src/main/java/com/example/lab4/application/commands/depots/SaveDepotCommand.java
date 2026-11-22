package com.example.lab4.application.commands.depots;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.cqrs.Command;

public record SaveDepotCommand(Depot depot) implements Command<Depot> {}
