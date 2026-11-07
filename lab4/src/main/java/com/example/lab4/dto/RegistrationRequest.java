package com.example.lab4.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.nio.charset.StandardCharsets;

public record RegistrationRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,64}",
                message = "Username must contain 3–64 letters, digits, dots, underscores or hyphens") String username,
        @NotBlank @Size(min = 8, max = 72)
        @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, format = "password") String password,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 40) String phone,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 255) String coordinates) {

    // BCrypt's input limit is measured in bytes, including for Unicode passwords.
    @JsonIgnore
    @AssertTrue(message = "Password must not exceed 72 UTF-8 bytes")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    @Override
    public String toString() { return "RegistrationRequest[username=" + username + ", password=REDACTED]"; }
}
