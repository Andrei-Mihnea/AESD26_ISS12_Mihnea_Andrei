package com.example.lab4.application.queries.depots;

import com.example.lab4.domain.entities.Depot;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetDepotsQuery() implements Query<List<Depot>> {}
