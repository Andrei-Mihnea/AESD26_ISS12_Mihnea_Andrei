package com.example.lab4.web;

import com.example.lab4.config.DevSecurityConfiguration;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(DevCollectionSecurityTest.TestConfiguration.class)
@WebAppConfiguration
@ActiveProfiles("dev")
class DevCollectionSecurityTest {
    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({DevSecurityConfiguration.class, MethodSecurityTest.TestConfiguration.class})
    static class TestConfiguration {
        @Bean JwtDecoder decoder() {
            JwtDecoder decoder = mock(JwtDecoder.class);
            for (String role : List.of("ADMIN", "CUSTOMER", "DRIVER", "GUEST")) {
                when(decoder.decode(role)).thenReturn(Jwt.withTokenValue(role).header("alg", "test")
                        .subject(role).build());
            }
            return decoder;
        }
        @Bean JwtAuthenticationConverter converter() {
            var converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(jwt -> List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getSubject())));
            return converter;
        }
    }

    @Autowired WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
    }

    @Test void anonymousCollectionReadsAreRejected() throws Exception {
        for (String prefix : List.of("/api", "/api/v1", "/api/v2")) {
            for (String resource : List.of("customers", "vehicles", "deliveries", "scenarios")) {
                String path = prefix + "/" + resource;
                mvc.perform(get(path)).andExpect(status().isUnauthorized());
                mvc.perform(head(path)).andExpect(status().isUnauthorized());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "CUSTOMER", "DRIVER", "GUEST"})
    void onlyAdminCanListCollectionsInDev(String role) throws Exception {
        for (String prefix : List.of("/api", "/api/v1", "/api/v2")) {
            for (String resource : List.of("customers", "vehicles", "deliveries", "scenarios")) {
                String path = prefix + "/" + resource;
                int expected = role.equals("ADMIN") ? 200 : 403;
                mvc.perform(get(path).queryParam("page", "0").header("Authorization", "Bearer " + role)).andExpect(status().is(expected));
                mvc.perform(head(path).header("Authorization", "Bearer " + role)).andExpect(status().is(expected));
            }
        }
    }

    @Test void otherDevEndpointsStillBypassAuthorization() throws Exception {
        mvc.perform(get("/api/v2/vehicles/1")).andExpect(status().isOk());
        mvc.perform(get("/api/v2/customers/1/deliveries")).andExpect(status().isOk());
        mvc.perform(put("/api/v2/vehicles/1").contentType("application/json")
                .content("{\"name\":\"Dev test\",\"capacity\":10,\"available\":true}")).andExpect(status().isOk());
        mvc.perform(delete("/api/v2/vehicles/1")).andExpect(status().isNoContent());
    }
}
