package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import com.example.lab4.domain.enums.Role;
import com.example.lab4.application.interfaces.DeliveryRepository;
import com.example.lab4.application.dto.DeliveryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetCustomerDeliveriesQueryHandler implements QueryHandler<GetCustomerDeliveriesQuery, List<DeliveryResponse>> {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository customers;

    public GetCustomerDeliveriesQueryHandler(DeliveryRepository deliveryRepository, UserRepository customers) {
        this.deliveryRepository = deliveryRepository;
        this.customers = customers;
    }

    @Override public Class<GetCustomerDeliveriesQuery> messageType() { return GetCustomerDeliveriesQuery.class; }

    @Override
    public List<DeliveryResponse> handle(GetCustomerDeliveriesQuery query) {
        Long customerId = query.customerId();
        customer(customerId);
        return deliveryRepository.findByCustomer_IdOrderByIdAsc(customerId).stream()
                .map(DeliveryResponse::from).toList();
    }

    private User customer(Long id) {
        return customers.findByIdAndRole(id, Role.CUSTOMER).orElseThrow(() ->
                new ResourceNotFoundException("Customer", id));
    }
}
