package com.example.lab4;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModularityVerificationTest {

    @Test
    void verifiesApplicationModuleStructure() {
        var modules = ApplicationModules.of(Lab4Application.class);
        assertTrue(modules.getModuleByName("optimization").isPresent(), "Optimization must be discovered as a module");
        modules.verify();
    }
}
