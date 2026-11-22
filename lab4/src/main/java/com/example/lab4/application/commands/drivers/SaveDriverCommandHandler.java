package com.example.lab4.application.commands.drivers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveDriverCommandHandler implements CommandHandler<SaveDriverCommand, User> {
    private final UserRepository users;

    public SaveDriverCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<SaveDriverCommand> messageType() { return SaveDriverCommand.class; }

    @Transactional
    @Override
    public User handle(SaveDriverCommand command) {
        User driver = command.driver();
        driver.requireRole(Role.DRIVER);
        return users.save(driver);
    }

}
