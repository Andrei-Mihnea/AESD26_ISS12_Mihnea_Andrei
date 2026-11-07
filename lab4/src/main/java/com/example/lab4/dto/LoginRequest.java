package com.example.lab4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 255) @Schema(example = "student") String username,
        @NotBlank @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, format = "password") String password) {
    @Override
    public String toString() { return "LoginRequest[username=" + username + ", password=REDACTED]"; }
}
