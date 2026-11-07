package com.example.lab4.web;

import com.example.lab4.config.MethodSecurityConfiguration;
import com.example.lab4.dto.VehicleRequest;
import com.example.lab4.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(MethodSecurityTest.TestConfiguration.class)
@ActiveProfiles("dev")
class DevProfileSecurityTest {
    @Autowired ApplicationContext context;
    @Autowired VehicleController vehicles;
    @Autowired CustomerController customers;
    @Autowired VehicleService service;

    @BeforeEach
    void prepare() {
        SecurityContextHolder.clearContext();
        reset(service);
    }

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void methodSecurityConfigurationIsAbsentInDev() {
        assertTrue(context.getBeansOfType(MethodSecurityConfiguration.class).isEmpty());
    }

    @Test
    void anonymousWritesBypassPreAuthorizeInDev() {
        VehicleRequest request = new VehicleRequest("Dev vehicle", 10, true);
        assertDoesNotThrow(() -> vehicles.update(1L, request));
        assertDoesNotThrow(() -> vehicles.delete(1L));
        verify(service).update(1L, request);
        verify(service).deleteExisting(1L);
    }

    @Test
    void anonymousPrivateReadsBypassClassLevelPreAuthorizeInDev() {
        assertDoesNotThrow(() -> customers.findDeliveries(1L));
    }
}
