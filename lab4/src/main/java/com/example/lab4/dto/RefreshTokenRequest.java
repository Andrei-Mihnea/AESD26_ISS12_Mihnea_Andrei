package com.example.lab4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenRequest(
        @NotBlank @Size(max = 512) @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) String refreshToken) {
    @Override public String toString() { return "RefreshTokenRequest[refreshToken=REDACTED]"; }
}
