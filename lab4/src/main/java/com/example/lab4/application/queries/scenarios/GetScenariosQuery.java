package com.example.lab4.application.queries.scenarios;

import com.example.lab4.application.dto.ScenarioResponse;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetScenariosQuery() implements Query<List<ScenarioResponse>> {}
