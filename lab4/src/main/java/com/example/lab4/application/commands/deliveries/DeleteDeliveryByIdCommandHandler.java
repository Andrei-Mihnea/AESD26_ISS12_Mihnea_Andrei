package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteDeliveryByIdCommandHandler implements CommandHandler<DeleteDeliveryByIdCommand, Void> {
    private final DeliveryRepository deliveryRepository;

    public DeleteDeliveryByIdCommandHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<DeleteDeliveryByIdCommand> messageType() { return DeleteDeliveryByIdCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteDeliveryByIdCommand command) {
        Long id = command.id();
        deliveryRepository.deleteById(id);
        return null;
    }

}
