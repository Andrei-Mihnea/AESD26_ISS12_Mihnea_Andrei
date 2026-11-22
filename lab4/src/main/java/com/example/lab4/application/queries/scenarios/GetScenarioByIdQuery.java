package com.example.lab4.application.queries.scenarios;

import com.example.lab4.application.dto.ScenarioResponse;
import com.example.lab4.application.cqrs.Query;

public record GetScenarioByIdQuery(Long id) implements Query<ScenarioResponse> {}
