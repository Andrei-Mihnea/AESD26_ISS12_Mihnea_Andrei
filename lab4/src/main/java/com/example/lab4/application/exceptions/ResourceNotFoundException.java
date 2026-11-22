package com.example.lab4.application.exceptions;

import java.util.UUID;

/** Raised when a requested resource or a referenced entity does not exist. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " " + id + " not found");
    }

    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " " + id + " not found");
    }
}
