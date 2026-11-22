package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import com.example.lab4.application.dto.DeliveryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetDeliveryByIdQueryHandler implements QueryHandler<GetDeliveryByIdQuery, DeliveryResponse> {
    private final DeliveryRepository deliveryRepository;

    public GetDeliveryByIdQueryHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<GetDeliveryByIdQuery> messageType() { return GetDeliveryByIdQuery.class; }

    @Override
    public DeliveryResponse handle(GetDeliveryByIdQuery query) {
        Long id = query.id();
        return DeliveryResponse.from(require(id));
    }

    private Delivery require(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Delivery", id));
    }
}
