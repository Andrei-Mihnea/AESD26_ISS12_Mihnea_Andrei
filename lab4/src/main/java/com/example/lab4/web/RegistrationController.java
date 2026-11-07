package com.example.lab4.web;

import com.example.lab4.dto.RegistrationRequest;
import com.example.lab4.dto.RegistrationResponse;
import com.example.lab4.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication")
public class RegistrationController {
    private final RegistrationService registration;

    public RegistrationController(RegistrationService registration) { this.registration = registration; }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Register a customer account", description = "Public registration creates an enabled CUSTOMER with a salted BCrypt password hash. "
            + "Roles cannot be selected. Usernames are case-sensitive. Passwords require at least 8 characters and at most 72 UTF-8 bytes. "
            + "After registration, POST username and password to /login to obtain a JWT.")
    @ApiResponse(responseCode = "201", description = "Customer account created")
    @ApiResponse(responseCode = "400", description = "Missing, malformed or invalid registration details")
    @ApiResponse(responseCode = "409", description = "Username or customer email already exists")
    public RegistrationResponse register(@Valid @RequestBody RegistrationRequest request) {
        return registration.register(request);
    }
}
