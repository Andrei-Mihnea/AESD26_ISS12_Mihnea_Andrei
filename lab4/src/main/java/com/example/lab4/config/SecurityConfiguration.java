package com.example.lab4.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!dev | prod")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {
    static final String[] ADMIN_LISTS = {
            "/api/customers", "/api/v1/customers", "/api/v2/customers",
            "/api/vehicles", "/api/v1/vehicles", "/api/v2/vehicles",
            "/api/deliveries", "/api/v1/deliveries", "/api/v2/deliveries",
            "/api/scenarios", "/api/v1/scenarios", "/api/v2/scenarios"
    };
    private static final String[] PUBLIC_READS = {
            "/api/vehicles", "/api/vehicles/**", "/api/v1/vehicles", "/api/v1/vehicles/**",
            "/api/v2/vehicles", "/api/v2/vehicles/**",
            "/api/scenarios", "/api/scenarios/**", "/api/v1/scenarios", "/api/v1/scenarios/**",
            "/api/v2/scenarios", "/api/v2/scenarios/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter converter) throws Exception {
        var unauthorized = new BearerTokenAuthenticationEntryPoint();
        var forbidden = new BearerTokenAccessDeniedHandler();
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/refresh", "/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        // Collection rules must precede the broader public vehicle/scenario read rules.
                        .requestMatchers(HttpMethod.GET, ADMIN_LISTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.HEAD, ADMIN_LISTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, PUBLIC_READS).permitAll()
                        .requestMatchers(HttpMethod.HEAD, PUBLIC_READS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "CUSTOMER", "DRIVER")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "CUSTOMER", "DRIVER")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole("ADMIN", "CUSTOMER", "DRIVER")
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(converter))
                        .authenticationEntryPoint(unauthorized).accessDeniedHandler(forbidden))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Authentication is header-only Bearer, never an automatically submitted cookie or Basic credential.
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler(forbidden))
                .headers(headers -> headers.cacheControl(cache -> cache.disable())
                        .addHeaderWriter(SecurityConfiguration::writeCacheHeaders));
        return http.build();
    }

    static void writeCacheHeaders(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean conditionalRead = request.getMethod().equals("GET") && path.startsWith("/api/")
                && (response.getStatus() == 200 || response.getStatus() == 304);
        response.setHeader(HttpHeaders.CACHE_CONTROL, conditionalRead ? "private, no-cache" : "no-store");
        if (path.startsWith("/api/")) response.addHeader(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION);
    }
}
