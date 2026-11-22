package com.example.lab4.application.commands.authentication;

import com.example.lab4.application.dto.RegistrationRequest;
import com.example.lab4.application.dto.RegistrationResponse;
import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.exceptions.RegistrationConflictException;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import com.example.lab4.application.interfaces.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Validated
@Transactional
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, RegistrationResponse> {
    private final UserRepository users;
    private final PasswordHasher passwords;

    public RegisterUserCommandHandler(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Override public Class<RegisterUserCommand> messageType() { return RegisterUserCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public RegistrationResponse handle(RegisterUserCommand command) {
        RegistrationRequest request = command.request();
        if (users.existsByUsername(request.username()) || users.existsByEmailAndRole(request.email(), Role.CUSTOMER)) {
            throw new RegistrationConflictException();
        }

        User user = User.customer(request.name(), request.email(), request.phone(), request.address(), request.coordinates());
        user.configureLogin(request.username(), passwords.encode(request.password()));
        users.saveAndFlush(user);
        return new RegistrationResponse(user.getId(), user.getUsername(), user.getRole());
    }

}
