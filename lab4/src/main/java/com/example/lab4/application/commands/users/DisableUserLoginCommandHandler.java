package com.example.lab4.application.commands.users;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import com.example.lab4.application.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DisableUserLoginCommandHandler implements CommandHandler<DisableUserLoginCommand, Void> {
    private final UserRepository users;

    public DisableUserLoginCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<DisableUserLoginCommand> messageType() { return DisableUserLoginCommand.class; }

    @Transactional
    @Override
    public Void handle(DisableUserLoginCommand command) {
        Long id = command.id();
        users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)).disableLogin();

        return null;
    }

}
