package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.interfaces.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteScenarioCommandHandler implements CommandHandler<DeleteScenarioCommand, Void> {
    private final ScenarioRepository scenarioRepository;

    public DeleteScenarioCommandHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<DeleteScenarioCommand> messageType() { return DeleteScenarioCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteScenarioCommand command) {
        Long id = command.id();
        scenarioRepository.delete(require(id));
        scenarioRepository.flush();

        return null;
    }

    private Scenario require(Long id) {
        return scenarioRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Scenario", id));
    }
}
