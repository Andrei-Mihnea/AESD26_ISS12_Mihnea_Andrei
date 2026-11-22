package com.example.lab4.application.commands.depots;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveDepotCommandHandler implements CommandHandler<SaveDepotCommand, Depot> {
    private final DepotRepository depotRepository;

    public SaveDepotCommandHandler(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override public Class<SaveDepotCommand> messageType() { return SaveDepotCommand.class; }

    @Transactional
    @Override
    public Depot handle(SaveDepotCommand command) {
        Depot depot = command.depot();
        return depotRepository.save(depot);
    }

}
