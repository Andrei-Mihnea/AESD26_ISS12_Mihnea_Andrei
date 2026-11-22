package com.example.lab4.application.queries.scenarios;

import com.example.lab4.domain.entities.Scenario;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record ListScenarioEntitiesQuery() implements Query<List<Scenario>> {}
