package com.example.lab4.exception;

public class RegistrationConflictException extends RuntimeException {
    public RegistrationConflictException() { super("Username or customer email is already in use."); }
}
