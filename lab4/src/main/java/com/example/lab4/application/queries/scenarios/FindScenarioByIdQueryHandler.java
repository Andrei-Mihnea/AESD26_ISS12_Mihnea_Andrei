package com.example.lab4.application.queries.scenarios;

import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.interfaces.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindScenarioByIdQueryHandler implements QueryHandler<FindScenarioByIdQuery, Optional<Scenario>> {
    private final ScenarioRepository scenarioRepository;

    public FindScenarioByIdQueryHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<FindScenarioByIdQuery> messageType() { return FindScenarioByIdQuery.class; }

    @Override
    public Optional<Scenario> handle(FindScenarioByIdQuery query) {
        Long id = query.id();
        return scenarioRepository.findById(id);
    }

}
