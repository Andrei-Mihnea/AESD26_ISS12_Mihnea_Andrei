package com.example.lab4.infrastructure.security;

import com.example.lab4.application.interfaces.AccessTokenIssuer;
import com.example.lab4.domain.entities.User;
import com.example.lab4.infrastructure.configuration.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class LocalJwtAccessTokenIssuer implements AccessTokenIssuer {
    private final JwtEncoder encoder;
    private final JwtProperties settings;
    public LocalJwtAccessTokenIssuer(JwtEncoder encoder, JwtProperties settings) { this.encoder = encoder; this.settings = settings; }
    @Override public IssuedToken issue(User user) {
        Instant now = Instant.now();
        Instant expires = now.plusSeconds(settings.ttlSeconds());
        var claims = JwtClaimsSet.builder().issuer(settings.issuer()).subject(user.getUsername())
                .audience(List.of(settings.audience())).issuedAt(now).notBefore(now).expiresAt(expires)
                .id(UUID.randomUUID().toString()).claim("uid", user.getId())
                .claim("roles", List.of(user.getRole().name())).claim("token_use", "access").build();
        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(), claims)).getTokenValue();
        return new IssuedToken(token, settings.ttlSeconds(), expires);
    }
}
