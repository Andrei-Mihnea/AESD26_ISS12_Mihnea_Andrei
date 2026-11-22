package com.example.lab4.application.queries.scenarios;

import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.interfaces.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class ListScenarioEntitiesQueryHandler implements QueryHandler<ListScenarioEntitiesQuery, List<Scenario>> {
    private final ScenarioRepository scenarioRepository;

    public ListScenarioEntitiesQueryHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<ListScenarioEntitiesQuery> messageType() { return ListScenarioEntitiesQuery.class; }

    @Override
    public List<Scenario> handle(ListScenarioEntitiesQuery query) {

        return scenarioRepository.findAll();
    }

}
