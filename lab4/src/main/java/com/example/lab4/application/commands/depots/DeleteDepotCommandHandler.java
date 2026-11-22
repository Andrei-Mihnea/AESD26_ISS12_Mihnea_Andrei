package com.example.lab4.application.commands.depots;

import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteDepotCommandHandler implements CommandHandler<DeleteDepotCommand, Void> {
    private final DepotRepository depotRepository;

    public DeleteDepotCommandHandler(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override public Class<DeleteDepotCommand> messageType() { return DeleteDepotCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteDepotCommand command) {
        Long id = command.id();
        depotRepository.deleteById(id);
        return null;
    }

}
