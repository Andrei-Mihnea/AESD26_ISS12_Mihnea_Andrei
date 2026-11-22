package com.example.lab4.application.queries.depots;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindDepotByNameQueryHandler implements QueryHandler<FindDepotByNameQuery, Optional<Depot>> {
    private final DepotRepository depotRepository;

    public FindDepotByNameQueryHandler(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override public Class<FindDepotByNameQuery> messageType() { return FindDepotByNameQuery.class; }

    @Override
    public Optional<Depot> handle(FindDepotByNameQuery query) {
        String name = query.name();
        return depotRepository.findByName(name);
    }

}
