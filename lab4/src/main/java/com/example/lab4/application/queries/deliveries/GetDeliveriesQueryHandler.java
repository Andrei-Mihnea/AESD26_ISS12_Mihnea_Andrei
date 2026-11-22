package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.interfaces.DeliveryRepository;
import com.example.lab4.application.dto.DeliveryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetDeliveriesQueryHandler implements QueryHandler<GetDeliveriesQuery, List<DeliveryResponse>> {
    private final DeliveryRepository deliveryRepository;

    public GetDeliveriesQueryHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<GetDeliveriesQuery> messageType() { return GetDeliveriesQuery.class; }

    @Override
    public List<DeliveryResponse> handle(GetDeliveriesQuery query) {

        return deliveryRepository.findAll().stream().map(DeliveryResponse::from).toList();
    }

}
