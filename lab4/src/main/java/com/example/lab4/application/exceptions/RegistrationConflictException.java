package com.example.lab4.application.exceptions;

public class RegistrationConflictException extends RuntimeException {
    public RegistrationConflictException() { super("Username or customer email is already in use."); }
}
