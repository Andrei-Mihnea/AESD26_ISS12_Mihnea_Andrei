package com.example.lab4.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ClassUtils;
import com.example.lab4.web.VehicleController;
import com.example.lab4.web.ScenarioController;
import com.example.lab4.web.LoginController;
import com.example.lab4.web.RegistrationController;

import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
public class SecurityOpenApiConfiguration {
    @Bean
    public GlobalOperationCustomizer roleDocumentation(Environment environment) {
        boolean dev = environment.acceptsProfiles(Profiles.of("dev & !prod"));
        return (operation, handler) -> {
            PreAuthorize authorization = handler.getMethodAnnotation(PreAuthorize.class);
            boolean adminOnly = authorization != null && authorization.value().equals("hasRole('ADMIN')");
            boolean read = handler.hasMethodAnnotation(GetMapping.class);
            if (dev && !(read && adminOnly)) {
                operation.setSecurity(List.of());
                return operation;
            }
            if (dev) operation.setSecurity(List.of(new SecurityRequirement().addList("bearerAuth")));
            Class<?> controller = ClassUtils.getUserClass(handler.getBeanType());
            if (controller.getPackageName().equals("com.example.lab4.web")
                    && controller != LoginController.class && controller != RegistrationController.class) {
                if (read && !adminOnly && (controller == VehicleController.class || controller == ScenarioController.class)) {
                    operation.setSecurity(List.of());
                } else {
                    operation.getResponses().addApiResponse("401", new ApiResponse().description("Missing or invalid Bearer token"));
                    if (adminOnly || !read) {
                        operation.setDescription((operation.getDescription() == null ? "" : operation.getDescription() + " ")
                                + (adminOnly ? "Requires the ADMIN role." : "Requires an ADMIN, CUSTOMER or DRIVER role."));
                        operation.getResponses().addApiResponse("403", new ApiResponse().description(adminOnly
                                ? "Authenticated user does not have the ADMIN role" : "Authenticated user has no application role"));
                    }
                }
            }
            return operation;
        };
    }

    @Bean
    public OpenAPI securityDocumentation(Environment environment, OidcProperties oidc) {
        OpenAPI api = new OpenAPI().components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
        if (!environment.acceptsProfiles(Profiles.of("dev & !prod"))) {
            api.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            if (oidc.enabled()) {
                api.getComponents().addSecuritySchemes("keycloak", new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(new io.swagger.v3.oas.models.security.OAuthFlows().authorizationCode(
                                new io.swagger.v3.oas.models.security.OAuthFlow().authorizationUrl(oidc.endpoint("auth"))
                                        .tokenUrl(oidc.endpoint("token"))
                                        .scopes(new io.swagger.v3.oas.models.security.Scopes().addString("openid", "OpenID Connect")))));
                api.addSecurityItem(new SecurityRequirement().addList("keycloak", List.of("openid")));
            }
        }
        return api;
    }
}
