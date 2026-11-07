package com.example.lab4.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, OidcProperties.class})
public class JwtConfiguration {
    @Bean
    public SecretKey jwtSigningKey(JwtProperties settings) {
        byte[] bytes;
        if (settings.secret() == null || settings.secret().isBlank()) {
            bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
        } else {
            bytes = Base64.getDecoder().decode(settings.secret());
            if (bytes.length < 32) throw new IllegalArgumentException("JWT_SECRET must decode to at least 32 bytes");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSigningKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSigningKey, JwtProperties settings, OidcProperties oidc) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSigningKey).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(settings.issuer()),
                new JwtClaimValidator<String>("token_use", "access"::equals),
                new JwtClaimValidator<Object>("aud", value -> value instanceof List<?> audiences && audiences.contains(settings.audience())),
                new JwtClaimValidator<String>("sub", value -> value != null && !value.isBlank()),
                new JwtClaimValidator<Object>("exp", value -> value instanceof Instant),
                new JwtClaimValidator<Object>("uid", value -> value instanceof Number id && id.longValue() > 0),
                new JwtClaimValidator<Object>("roles", value -> value instanceof List<?> roles && !roles.isEmpty()
                        && roles.stream().allMatch(role -> role instanceof String && Set.of("ADMIN", "CUSTOMER", "DRIVER").contains(role)))));
        if (!oidc.enabled()) return decoder;
        if (oidc.issuerUri().equals(settings.issuer())) throw new IllegalArgumentException("Local and OIDC issuers must be different");
        NimbusJwtDecoder external = NimbusJwtDecoder.withJwkSetUri(oidc.endpoint("certs"))
                .jwsAlgorithm(SignatureAlgorithm.RS256).build();
        external.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(oidc.issuerUri()),
                new JwtClaimValidator<Object>("exp", value -> value instanceof Instant),
                new JwtClaimValidator<String>("sub", value -> value != null && !value.isBlank()),
                new JwtClaimValidator<String>("typ", "Bearer"::equals),
                new JwtClaimValidator<Object>("aud", value -> value instanceof List<?> audiences && audiences.contains(oidc.audience()))));
        return token -> {
            try {
                // An unverified issuer only selects between fixed, configured validators; it never supplies a URL.
                String issuer = JWTParser.parse(token).getJWTClaimsSet().getIssuer();
                if (settings.issuer().equals(issuer)) return decoder.decode(token);
                if (oidc.issuerUri().equals(issuer)) return external.decode(token);
                throw new BadJwtException("Untrusted issuer");
            } catch (java.text.ParseException exception) { throw new BadJwtException("Malformed JWT", exception); }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(JwtProperties settings, OidcProperties oidc) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new ApplicationJwtAuthoritiesConverter(settings, oidc));
        return converter;
    }
}
