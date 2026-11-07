package com.example.lab4.dto;

import com.example.lab4.entity.Role;
import java.time.Instant;

public record LoginResponse(String accessToken, String tokenType, long expiresIn, Instant expiresAt,
                            String username, Role role, String refreshToken, Instant refreshExpiresAt) {
    @Override
    public String toString() { return "LoginResponse[accessToken=REDACTED, username=" + username + ", role=" + role + "]"; }
}
