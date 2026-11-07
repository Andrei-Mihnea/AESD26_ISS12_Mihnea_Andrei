package com.example.lab4.config;

import com.example.lab4.service.DatabaseUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseAuthenticationConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Encodes with salted BCrypt and the {bcrypt} prefix, also used when verifying login passwords.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider databaseAuthenticationProvider(DatabaseUserDetailsService users,
                                                                     PasswordEncoder passwords) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(passwords);
        return provider;
    }
}
