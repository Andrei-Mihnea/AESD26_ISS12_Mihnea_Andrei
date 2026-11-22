package com.example.lab4.application.commands.deliveries;

import com.example.lab4.application.dto.DeliveryRequest;
import com.example.lab4.application.dto.DeliveryResponse;
import com.example.lab4.application.cqrs.Command;

public record CreateDeliveryCommand(DeliveryRequest request) implements Command<DeliveryResponse> {}
