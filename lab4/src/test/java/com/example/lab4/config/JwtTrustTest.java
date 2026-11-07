package com.example.lab4.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtTrustTest {
    private final JwtProperties local = new JwtProperties("lab4", "lab4-api", 900, "", 604800);
    private final OidcProperties oidc = new OidcProperties("http://localhost:18081/realms/lab4", "lab4-api", "lab4-web");
    private final ApplicationJwtAuthoritiesConverter roles = new ApplicationJwtAuthoritiesConverter(local, oidc);

    private Jwt jwt(String issuer, Map<String, Object> claims) {
        return Jwt.withTokenValue("test").header("alg", "RS256").claim("iss", issuer).claims(map -> map.putAll(claims)).build();
    }

    @Test void mapsOnlyKnownRealmAndApiClientRoles() {
        var jwt = jwt(oidc.issuerUri(), Map.of("realm_access", Map.of("roles", List.of("ADMIN", "offline_access")),
                "resource_access", Map.of("lab4-api", Map.of("roles", List.of("CUSTOMER", "DRIVER")),
                        "unrelated-client", Map.of("roles", List.of("ROOT")))));
        assertEquals(List.of("ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_DRIVER"), roles.convert(jwt).stream().map(Object::toString).toList());
    }

    @Test void ignoresLocalRoleClaimOnExternalTokens() {
        assertTrue(roles.convert(jwt(oidc.issuerUri(), Map.of("roles", List.of("ADMIN")))).isEmpty());
    }

    @Test void ignoresRolesFromOtherClientsAndMalformedClaims() {
        assertTrue(roles.convert(jwt(oidc.issuerUri(), Map.of("realm_access", "ADMIN",
                "resource_access", Map.of("another-client", Map.of("roles", List.of("ADMIN")))))).isEmpty());
    }

    @Test void ignoresUnknownIssuers() {
        assertTrue(roles.convert(jwt("https://untrusted.example", Map.of("roles", List.of("ADMIN"),
                "realm_access", Map.of("roles", List.of("ADMIN"))))).isEmpty());
    }

    @Test void mapsLocalRolesWithoutTrustingExternalClaims() {
        assertEquals(List.of("ROLE_CUSTOMER"), roles.convert(jwt(local.issuer(), Map.of("roles", List.of("CUSTOMER"),
                "realm_access", Map.of("roles", List.of("ADMIN"))))).stream().map(Object::toString).toList());
    }

    @Test void localDecoderAcceptsAccessTokensOnly() {
        var configuration = new JwtConfiguration();
        var key = new SecretKeySpec(new byte[32], "HmacSHA256");
        var encoder = configuration.jwtEncoder(key);
        var decoder = configuration.jwtDecoder(key, local, new OidcProperties("", "lab4-api", "lab4-web"));
        for (String use : List.of("access", "refresh", "id", "")) {
            var claims = JwtClaimsSet.builder().issuer("lab4").subject("user").audience(List.of("lab4-api"))
                    .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).claim("uid", 1)
                    .claim("roles", List.of("CUSTOMER")).claim("token_use", use).build();
            String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
            if (use.equals("access")) assertDoesNotThrow(() -> decoder.decode(token));
            else assertThrows(JwtValidationException.class, () -> decoder.decode(token));
        }
    }
    @Test void validatesExternalSignaturesIssuerAudienceExpiryAndTokenPurpose() throws Exception {
        var key = new com.nimbusds.jose.jwk.gen.RSAKeyGenerator(2048).keyID("test-key").generate();
        var server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("localhost", 0), 0);
        byte[] jwks = new com.nimbusds.jose.jwk.JWKSet(key.toPublicJWK()).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        server.createContext("/realms/lab4/protocol/openid-connect/certs", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jwks.length);
            try (var out = exchange.getResponseBody()) { out.write(jwks); }
        });
        server.start();
        try {
            String issuer = "http://localhost:" + server.getAddress().getPort() + "/realms/lab4";
            var settings = new OidcProperties(issuer, "lab4-api", "lab4-web");
            var configuration = new JwtConfiguration();
            var decoder = configuration.jwtDecoder(new SecretKeySpec(new byte[32], "HmacSHA256"), local, settings);
            var encoder = new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableJWKSet<>(new com.nimbusds.jose.jwk.JWKSet(key)));
            Map<String, Object> valid = new HashMap<>(Map.of("iss", issuer, "sub", "external-user", "aud", List.of("lab4-api"),
                    "exp", Instant.now().plusSeconds(300), "typ", "Bearer", "realm_access", Map.of("roles", List.of("CUSTOMER"))));
            for (String mutation : List.of("valid", "audience", "issuer", "expired", "missing-expiry", "id-token", "refresh-token")) {
                Map<String, Object> claims = new HashMap<>(valid);
                switch (mutation) {
                    case "audience" -> claims.put("aud", List.of("another-api"));
                    case "issuer" -> claims.put("iss", "https://untrusted.example");
                    case "expired" -> claims.put("exp", Instant.now().minusSeconds(120));
                    case "missing-expiry" -> claims.remove("exp");
                    case "id-token" -> claims.put("typ", "ID");
                    case "refresh-token" -> claims.put("typ", "Refresh");
                }
                String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256).keyId("test-key").build(),
                        JwtClaimsSet.builder().claims(map -> map.putAll(claims)).build())).getTokenValue();
                if (mutation.equals("valid")) assertEquals("external-user", decoder.decode(token).getSubject());
                else assertThrows(JwtException.class, () -> decoder.decode(token), mutation);
            }
            var otherKey = new com.nimbusds.jose.jwk.gen.RSAKeyGenerator(2048).keyID("test-key").generate();
            var attacker = new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableJWKSet<>(new com.nimbusds.jose.jwk.JWKSet(otherKey)));
            String forged = attacker.encode(JwtEncoderParameters.from(JwsHeader.with(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256).keyId("test-key").build(),
                    JwtClaimsSet.builder().claims(map -> map.putAll(valid)).build())).getTokenValue();
            assertThrows(JwtException.class, () -> decoder.decode(forged));
        } finally { server.stop(0); }
    }

}
