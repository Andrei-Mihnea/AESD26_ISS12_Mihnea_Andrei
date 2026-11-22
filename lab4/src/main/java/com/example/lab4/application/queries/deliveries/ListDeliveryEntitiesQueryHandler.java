package com.example.lab4.application.queries.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class ListDeliveryEntitiesQueryHandler implements QueryHandler<ListDeliveryEntitiesQuery, List<Delivery>> {
    private final DeliveryRepository deliveryRepository;

    public ListDeliveryEntitiesQueryHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<ListDeliveryEntitiesQuery> messageType() { return ListDeliveryEntitiesQuery.class; }

    @Override
    public List<Delivery> handle(ListDeliveryEntitiesQuery query) {

        return deliveryRepository.findAll();
    }

}
