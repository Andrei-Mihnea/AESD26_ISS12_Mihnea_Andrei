package com.example.lab4.application.interfaces;
import com.example.lab4.domain.entities.User;
import java.time.Instant;
public interface AccessTokenIssuer {
    IssuedToken issue(User user);
    record IssuedToken(String value, long expiresIn, Instant expiresAt) {}
}
