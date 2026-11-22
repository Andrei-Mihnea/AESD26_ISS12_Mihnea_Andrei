package com.example.lab4.application.queries.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.domain.valueobjects.DeliverySearchCriteria;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class SearchDeliveriesQueryHandler implements QueryHandler<SearchDeliveriesQuery, List<Delivery>> {
    private final DeliveryRepository deliveryRepository;

    public SearchDeliveriesQueryHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<SearchDeliveriesQuery> messageType() { return SearchDeliveriesQuery.class; }

    @Override
    public List<Delivery> handle(SearchDeliveriesQuery query) {
        DeliverySearchCriteria criteria = query.criteria();
        return deliveryRepository.search(criteria);
    }

}
