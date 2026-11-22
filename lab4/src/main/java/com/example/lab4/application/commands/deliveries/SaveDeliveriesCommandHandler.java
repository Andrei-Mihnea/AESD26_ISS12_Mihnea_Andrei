package com.example.lab4.application.commands.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveDeliveriesCommandHandler implements CommandHandler<SaveDeliveriesCommand, List<Delivery>> {
    private final DeliveryRepository deliveryRepository;

    public SaveDeliveriesCommandHandler(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override public Class<SaveDeliveriesCommand> messageType() { return SaveDeliveriesCommand.class; }

    @Transactional
    @Override
    public List<Delivery> handle(SaveDeliveriesCommand command) {
        Iterable<Delivery> deliveries = command.deliveries();
        return deliveryRepository.saveAll(deliveries);
    }

}
