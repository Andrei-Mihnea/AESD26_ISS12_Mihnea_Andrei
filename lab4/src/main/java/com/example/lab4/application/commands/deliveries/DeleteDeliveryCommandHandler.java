package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteDeliveryCommandHandler implements CommandHandler<DeleteDeliveryCommand, Void> {
    private final DeliveryRepository deliveryRepository;

    public DeleteDeliveryCommandHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<DeleteDeliveryCommand> messageType() { return DeleteDeliveryCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteDeliveryCommand command) {
        Long id = command.id();
        deliveryRepository.delete(require(id));
        deliveryRepository.flush();

        return null;
    }

    private Delivery require(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Delivery", id));
    }
}
