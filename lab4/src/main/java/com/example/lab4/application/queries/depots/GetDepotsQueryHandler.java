package com.example.lab4.application.queries.depots;

import com.example.lab4.domain.entities.Depot;
import com.example.lab4.application.interfaces.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetDepotsQueryHandler implements QueryHandler<GetDepotsQuery, List<Depot>> {
    private final DepotRepository depotRepository;

    public GetDepotsQueryHandler(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override public Class<GetDepotsQuery> messageType() { return GetDepotsQuery.class; }

    @Override
    public List<Depot> handle(GetDepotsQuery query) {

        return depotRepository.findAll();
    }

}
