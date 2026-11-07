package com.example.lab4.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String issuer, String audience, long ttlSeconds, String secret, long refreshTtlSeconds) {
    public JwtProperties {
        if (issuer == null || issuer.isBlank() || audience == null || audience.isBlank() || ttlSeconds < 1 || refreshTtlSeconds < 1) {
            throw new IllegalArgumentException("JWT issuer, audience, and a positive TTL are required");
        }
    }

    @Override
    public String toString() { return "JwtProperties[issuer=" + issuer + ", audience=" + audience + ", ttlSeconds=" + ttlSeconds + ", secret=REDACTED]"; }
}
