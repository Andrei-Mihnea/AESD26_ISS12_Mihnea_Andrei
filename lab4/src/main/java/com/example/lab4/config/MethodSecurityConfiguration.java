package com.example.lab4.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@Profile("!dev | prod")
@EnableMethodSecurity
public class MethodSecurityConfiguration {
}
