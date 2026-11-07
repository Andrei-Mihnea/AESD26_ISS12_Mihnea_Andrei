package com.example.lab4.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.net.URI;

@ConfigurationProperties("app.security.oidc")
public record OidcProperties(String issuerUri, String audience, String clientId) {
    public OidcProperties {
        if (issuerUri != null && !issuerUri.isBlank()) {
            URI uri = URI.create(issuerUri);
            if (!uri.isAbsolute() || uri.getHost() == null || uri.getQuery() != null || uri.getFragment() != null
                    || uri.getUserInfo() != null || !(uri.getScheme().equals("https") || uri.getScheme().equals("http"))
                    || audience == null || audience.isBlank() || clientId == null || clientId.isBlank()) {
                throw new IllegalArgumentException("OIDC issuer, audience and client ID must be valid");
            }
        }
    }
    public boolean enabled() { return issuerUri != null && !issuerUri.isBlank(); }
    public String endpoint(String name) { return issuerUri.replaceAll("/$", "") + "/protocol/openid-connect/" + name; }
}
