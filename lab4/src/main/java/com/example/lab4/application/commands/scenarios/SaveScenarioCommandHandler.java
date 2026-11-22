package com.example.lab4.application.commands.scenarios;

import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.interfaces.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveScenarioCommandHandler implements CommandHandler<SaveScenarioCommand, Scenario> {
    private final ScenarioRepository scenarioRepository;

    public SaveScenarioCommandHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<SaveScenarioCommand> messageType() { return SaveScenarioCommand.class; }

    @Transactional
    @Override
    public Scenario handle(SaveScenarioCommand command) {
        Scenario scenario = command.scenario();
        return scenarioRepository.save(scenario);
    }

}
