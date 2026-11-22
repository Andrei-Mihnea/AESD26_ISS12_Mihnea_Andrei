package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.dto.DeliveryRequest;
import com.example.lab4.application.dto.DeliveryResponse;
import com.example.lab4.application.cqrs.Command;

public record UpdateDeliveryCommand(Long id, DeliveryRequest request) implements Command<DeliveryResponse> {}
