package com.example.lab4.infrastructure.security;

import com.example.lab4.application.interfaces.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final PasswordEncoder encoder;
    public BCryptPasswordHasher(PasswordEncoder encoder) { this.encoder = encoder; }
    @Override public String encode(String rawPassword) { return encoder.encode(rawPassword); }
}
