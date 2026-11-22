package com.example.lab4.application.queries.depots;

import com.example.lab4.domain.entities.Depot;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record FindDepotByNameQuery(String name) implements Query<Optional<Depot>> {}
