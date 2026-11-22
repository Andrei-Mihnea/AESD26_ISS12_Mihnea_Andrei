package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class DeliveryExistsForCustomerWithNotesQueryHandler implements QueryHandler<DeliveryExistsForCustomerWithNotesQuery, Boolean> {
    private final DeliveryRepository deliveryRepository;

    public DeliveryExistsForCustomerWithNotesQueryHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<DeliveryExistsForCustomerWithNotesQuery> messageType() { return DeliveryExistsForCustomerWithNotesQuery.class; }

    @Override
    public Boolean handle(DeliveryExistsForCustomerWithNotesQuery query) {
        Long customerId = query.customerId();
        String notes = query.notes();
        return deliveryRepository.existsByCustomer_IdAndNotes(customerId, notes);
    }

}
