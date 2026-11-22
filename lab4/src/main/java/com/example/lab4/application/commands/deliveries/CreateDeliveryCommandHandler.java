package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.User;
import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.UserRepository;
import com.example.lab4.domain.enums.Role;
import com.example.lab4.application.interfaces.DeliveryRepository;
import com.example.lab4.application.dto.DeliveryRequest;
import com.example.lab4.application.dto.DeliveryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class CreateDeliveryCommandHandler implements CommandHandler<CreateDeliveryCommand, DeliveryResponse> {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository customers;

    public CreateDeliveryCommandHandler(DeliveryRepository deliveryRepository, UserRepository customers) {
        this.deliveryRepository = deliveryRepository;
        this.customers = customers;
    }

    @Override public Class<CreateDeliveryCommand> messageType() { return CreateDeliveryCommand.class; }

    @Transactional
    @Override
    public DeliveryResponse handle(CreateDeliveryCommand command) {
        DeliveryRequest request = command.request();
        return DeliveryResponse.from(deliveryRepository.save(new Delivery(customer(request.customerId()),
                request.quantity(), request.notes(), request.timestamp())));
    }

    private User customer(Long id) {
        return customers.findByIdAndRole(id, Role.CUSTOMER).orElseThrow(() ->
                new ResourceNotFoundException("Customer", id));
    }
}
