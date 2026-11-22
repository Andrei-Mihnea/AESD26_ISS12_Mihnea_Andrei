package com.example.lab4.application.queries.scenarios;

import com.example.lab4.domain.entities.Scenario;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record FindScenarioByIdQuery(Long id) implements Query<Optional<Scenario>> {}
