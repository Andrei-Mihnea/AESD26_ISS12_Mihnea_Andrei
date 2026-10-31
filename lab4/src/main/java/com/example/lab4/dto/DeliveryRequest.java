package com.example.lab4.dto;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record DeliveryRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Integer quantity,
        String notes,
        @NotNull OffsetDateTime timestamp) {
}
