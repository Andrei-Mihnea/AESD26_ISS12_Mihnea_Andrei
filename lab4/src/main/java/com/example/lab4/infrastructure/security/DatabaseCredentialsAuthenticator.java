package com.example.lab4.infrastructure.security;

import com.example.lab4.application.interfaces.CredentialsAuthenticator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCredentialsAuthenticator implements CredentialsAuthenticator {
    private final DaoAuthenticationProvider provider;
    public DatabaseCredentialsAuthenticator(DaoAuthenticationProvider provider) { this.provider = provider; }
    @Override public String authenticate(String username, String password) {
        return provider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, password)).getName();
    }
}
