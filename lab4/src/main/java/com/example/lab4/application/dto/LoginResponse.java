package com.example.lab4.application.dto;

import com.example.lab4.domain.enums.Role;
import java.time.Instant;

public record LoginResponse(String accessToken, String tokenType, long expiresIn, Instant expiresAt,
                            String username, Role role, String refreshToken, Instant refreshExpiresAt) {
    @Override
    public String toString() { return "LoginResponse[accessToken=REDACTED, username=" + username + ", role=" + role + "]"; }
}
