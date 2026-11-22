package com.example.lab4.application.commands.users;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import com.example.lab4.application.exceptions.ResourceNotFoundException;
import com.example.lab4.application.interfaces.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class ConfigureUserLoginCommandHandler implements CommandHandler<ConfigureUserLoginCommand, Void> {
    private final UserRepository users;
    private final PasswordHasher passwords;

    public ConfigureUserLoginCommandHandler(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Override public Class<ConfigureUserLoginCommand> messageType() { return ConfigureUserLoginCommand.class; }

    @Transactional
    @Override
    public Void handle(ConfigureUserLoginCommand command) {
        Long id = command.id();
        String username = command.username();
        String rawPassword = command.rawPassword();
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("Password is required");
        User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.configureLogin(username, passwords.encode(rawPassword));

        return null;
    }

}
