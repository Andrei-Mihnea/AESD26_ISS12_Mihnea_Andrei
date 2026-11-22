package com.example.lab4.application.dto;

import com.example.lab4.domain.entities.Delivery;
import java.time.OffsetDateTime;

public record DeliveryResponse(Long id, Long customerId, Integer quantity, String notes, OffsetDateTime timestamp) {
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(delivery.getId(), delivery.getCustomer().getId(), delivery.getQuantity(),
                delivery.getNotes(), delivery.getTimestamp());
    }
}
