package com.example.lab4.application.queries.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindDeliveryByIdQueryHandler implements QueryHandler<FindDeliveryByIdQuery, Optional<Delivery>> {
    private final DeliveryRepository deliveryRepository;

    public FindDeliveryByIdQueryHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<FindDeliveryByIdQuery> messageType() { return FindDeliveryByIdQuery.class; }

    @Override
    public Optional<Delivery> handle(FindDeliveryByIdQuery query) {
        Long id = query.id();
        return deliveryRepository.findById(id);
    }

}
