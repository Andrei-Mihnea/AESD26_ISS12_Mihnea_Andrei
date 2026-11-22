package com.example.lab4.application.queries.scenarios;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.interfaces.ScenarioRepository;
import com.example.lab4.application.dto.ScenarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetScenarioByIdQueryHandler implements QueryHandler<GetScenarioByIdQuery, ScenarioResponse> {
    private final ScenarioRepository scenarioRepository;

    public GetScenarioByIdQueryHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<GetScenarioByIdQuery> messageType() { return GetScenarioByIdQuery.class; }

    @Override
    public ScenarioResponse handle(GetScenarioByIdQuery query) {
        Long id = query.id();
        return ScenarioResponse.from(require(id));
    }

    private Scenario require(Long id) {
        return scenarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Scenario", id));
    }
}
