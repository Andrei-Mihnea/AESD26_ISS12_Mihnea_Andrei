package com.example.lab4.application.commands.depots;

import com.example.lab4.domain.entities.Depot;
import java.util.List;
import com.example.lab4.application.cqrs.Command;

public record SaveDepotsCommand(Iterable<Depot> depots) implements Command<List<Depot>> {}
