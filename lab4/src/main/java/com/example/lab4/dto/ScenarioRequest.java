package com.example.lab4.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

public record ScenarioRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull Set<@NotNull @Positive Long> vehicleIds,
        @NotNull Set<@NotNull @Positive Long> deliveryIds) {
}
