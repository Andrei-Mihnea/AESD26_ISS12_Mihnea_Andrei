package com.example.lab4.application.commands.depots;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveDepotsCommandHandler implements CommandHandler<SaveDepotsCommand, List<Depot>> {
    private final DepotRepository depotRepository;

    public SaveDepotsCommandHandler(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override public Class<SaveDepotsCommand> messageType() { return SaveDepotsCommand.class; }

    @Transactional
    @Override
    public List<Depot> handle(SaveDepotsCommand command) {
        Iterable<Depot> depots = command.depots();
        return depotRepository.saveAll(depots);
    }

}
