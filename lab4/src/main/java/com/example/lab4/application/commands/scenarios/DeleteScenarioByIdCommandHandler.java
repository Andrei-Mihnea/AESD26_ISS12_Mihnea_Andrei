package com.example.lab4.application.commands.scenarios;

import com.example.lab4.application.interfaces.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteScenarioByIdCommandHandler implements CommandHandler<DeleteScenarioByIdCommand, Void> {
    private final ScenarioRepository scenarioRepository;

    public DeleteScenarioByIdCommandHandler(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override public Class<DeleteScenarioByIdCommand> messageType() { return DeleteScenarioByIdCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteScenarioByIdCommand command) {
        Long id = command.id();
        scenarioRepository.deleteById(id);
        return null;
    }

}
