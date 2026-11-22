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
public class UpdateDeliveryCommandHandler implements CommandHandler<UpdateDeliveryCommand, DeliveryResponse> {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository customers;

    public UpdateDeliveryCommandHandler(DeliveryRepository deliveryRepository, UserRepository customers) {
        this.deliveryRepository = deliveryRepository;
        this.customers = customers;
    }

    @Override public Class<UpdateDeliveryCommand> messageType() { return UpdateDeliveryCommand.class; }

    @Transactional
    @Override
    public DeliveryResponse handle(UpdateDeliveryCommand command) {
        Long id = command.id();
        DeliveryRequest request = command.request();
        Delivery delivery = require(id);
        delivery.updateDetails(customer(request.customerId()), request.quantity(), request.notes(), request.timestamp());
        return DeliveryResponse.from(delivery);
    }

    private Delivery require(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Delivery", id));
    }

    private User customer(Long id) {
        return customers.findByIdAndRole(id, Role.CUSTOMER).orElseThrow(() ->
                new ResourceNotFoundException("Customer", id));
    }
}
