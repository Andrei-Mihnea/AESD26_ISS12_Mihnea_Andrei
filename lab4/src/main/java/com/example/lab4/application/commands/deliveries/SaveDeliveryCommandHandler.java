package com.example.lab4.application.commands.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveDeliveryCommandHandler implements CommandHandler<SaveDeliveryCommand, Delivery> {
    private final DeliveryRepository deliveryRepository;

    public SaveDeliveryCommandHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<SaveDeliveryCommand> messageType() { return SaveDeliveryCommand.class; }

    @Transactional
    @Override
    public Delivery handle(SaveDeliveryCommand command) {
        Delivery delivery = command.delivery();
        return deliveryRepository.save(delivery);
    }

}
