package com.example.lab4.application.queries.depots;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindDepotByIdQueryHandler implements QueryHandler<FindDepotByIdQuery, Optional<Depot>> {
    private final DepotRepository depotRepository;

    public FindDepotByIdQueryHandler(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override public Class<FindDepotByIdQuery> messageType() { return FindDepotByIdQuery.class; }

    @Override
    public Optional<Depot> handle(FindDepotByIdQuery query) {
        Long id = query.id();
        return depotRepository.findById(id);
    }

}
