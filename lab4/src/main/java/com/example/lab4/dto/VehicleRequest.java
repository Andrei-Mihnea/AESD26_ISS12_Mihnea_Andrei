package com.example.lab4.dto;

import jakarta.validation.constraints.*;

public record VehicleRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull @Positive Integer capacity,
        @NotNull Boolean available) {
}
