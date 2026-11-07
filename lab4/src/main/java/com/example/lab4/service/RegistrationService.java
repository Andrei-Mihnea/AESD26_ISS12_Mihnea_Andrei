package com.example.lab4.service;

import com.example.lab4.dto.RegistrationRequest;
import com.example.lab4.dto.RegistrationResponse;
import com.example.lab4.entity.Role;
import com.example.lab4.entity.User;
import com.example.lab4.exception.RegistrationConflictException;
import com.example.lab4.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RegistrationService {
    private final UserRepository users;
    private final PasswordEncoder passwords;

    public RegistrationService(UserRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    public RegistrationResponse register(@Valid RegistrationRequest request) {
        if (users.existsByUsername(request.username()) || users.existsByEmailAndRole(request.email(), Role.CUSTOMER)) {
            throw new RegistrationConflictException();
        }
        // Public registration always creates a new CUSTOMER, never provisions an existing identity.
        User user = User.customer(request.name(), request.email(), request.phone(), request.address(), request.coordinates());
        user.configureLogin(request.username(), passwords.encode(request.password()));
        users.saveAndFlush(user); // Database unique constraints also protect concurrent registrations.
        return new RegistrationResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
