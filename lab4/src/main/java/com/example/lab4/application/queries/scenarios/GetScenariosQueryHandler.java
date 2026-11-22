package com.example.lab4.application.queries.scenarios;

import com.example.lab4.application.interfaces.ScenarioRepository;
import com.example.lab4.application.dto.ScenarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetScenariosQueryHandler implements QueryHandler<GetScenariosQuery, List<ScenarioResponse>> {
    private final ScenarioRepository scenarioRepository;

    public GetScenariosQueryHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<GetScenariosQuery> messageType() { return GetScenariosQuery.class; }

    @Override
    public List<ScenarioResponse> handle(GetScenariosQuery query) {

        return scenarioRepository.findAll().stream().map(ScenarioResponse::from).toList();
    }

}
