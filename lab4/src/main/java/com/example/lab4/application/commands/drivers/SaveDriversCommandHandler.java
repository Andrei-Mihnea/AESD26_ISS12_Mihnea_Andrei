package com.example.lab4.application.commands.drivers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveDriversCommandHandler implements CommandHandler<SaveDriversCommand, List<User>> {
    private final UserRepository users;

    public SaveDriversCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<SaveDriversCommand> messageType() { return SaveDriversCommand.class; }

    @Transactional
    @Override
    public List<User> handle(SaveDriversCommand command) {
        Iterable<User> drivers = command.drivers();
        List<User> selected = new ArrayList<>();
        drivers.forEach(driver -> { driver.requireRole(Role.DRIVER); selected.add(driver); });
        return users.saveAll(selected);
    }

}
