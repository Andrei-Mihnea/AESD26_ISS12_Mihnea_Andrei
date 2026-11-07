package com.example.lab4.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidRefreshTokenException extends BadCredentialsException {
    public InvalidRefreshTokenException() { super("Invalid or expired refresh token"); }
}
