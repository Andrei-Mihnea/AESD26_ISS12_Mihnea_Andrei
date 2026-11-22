package com.example.lab4.api.controllers;

import com.example.lab4.api.configuration.DevSecurityConfiguration;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
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
    static class TestConfiguration {}

    @Autowired WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
    }

    @Test void anonymousCollectionReadsBypassSecurity() throws Exception {
        for (String prefix : List.of("/api", "/api/v1", "/api/v2")) {
            for (String resource : List.of("customers", "vehicles", "deliveries", "scenarios")) {
                String path = prefix + "/" + resource;
                mvc.perform(get(path)).andExpect(status().isOk());
                mvc.perform(head(path)).andExpect(status().isOk());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"CUSTOMER", "DRIVER", "invalid.jwt.token", "expired-token"})
    void bearerHeadersAreIgnoredInDev(String role) throws Exception {
        for (String prefix : List.of("/api", "/api/v1", "/api/v2")) {
            for (String resource : List.of("customers", "vehicles", "deliveries", "scenarios")) {
                String path = prefix + "/" + resource;
                mvc.perform(get(path).queryParam("page", "0").header("Authorization", "Bearer " + role)).andExpect(status().isOk());
                mvc.perform(head(path).header("Authorization", "Bearer " + role)).andExpect(status().isOk());
            }
        }
    }

    @Test void otherDevEndpointsStillBypassAuthorization() throws Exception {
        // Validation still runs; this must reach MVC and return 400 rather than a security rejection.
        mvc.perform(post("/api/v2/vehicles").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v2/vehicles/1")).andExpect(status().isOk());
        mvc.perform(get("/api/v2/customers/1/deliveries")).andExpect(status().isOk());
        mvc.perform(put("/api/v2/vehicles/1").contentType("application/json")
                .content("{\"name\":\"Dev test\",\"capacity\":10,\"available\":true}")).andExpect(status().isOk());
        mvc.perform(delete("/api/v2/vehicles/1")).andExpect(status().isNoContent());
    }
}
