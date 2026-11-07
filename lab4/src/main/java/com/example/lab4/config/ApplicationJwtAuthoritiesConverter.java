package com.example.lab4.config;

import com.example.lab4.entity.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.*;

public class ApplicationJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final JwtProperties local;
    private final OidcProperties oidc;
    public ApplicationJwtAuthoritiesConverter(JwtProperties local, OidcProperties oidc) { this.local = local; this.oidc = oidc; }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        if (local.issuer().equals(jwt.getClaimAsString("iss"))) {
            add(roles, jwt.getClaims().get("roles"));
        } else if (oidc.enabled() && oidc.issuerUri().equals(jwt.getClaimAsString("iss"))) {
            if (jwt.getClaims().get("realm_access") instanceof Map<?, ?> realm) add(roles, realm.get("roles"));
            if (jwt.getClaims().get("resource_access") instanceof Map<?, ?> clients
                    && clients.get(oidc.audience()) instanceof Map<?, ?> client) add(roles, client.get("roles"));
        }
        Set<String> allowed = Set.of(Role.ADMIN.name(), Role.CUSTOMER.name(), Role.DRIVER.name());
        return roles.stream().filter(allowed::contains).sorted()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }

    private void add(Set<String> target, Object value) {
        if (value instanceof Collection<?> values) values.stream().filter(String.class::isInstance).map(String.class::cast).forEach(target::add);
    }
}
