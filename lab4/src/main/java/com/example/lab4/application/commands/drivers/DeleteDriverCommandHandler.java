package com.example.lab4.application.commands.drivers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteDriverCommandHandler implements CommandHandler<DeleteDriverCommand, Void> {
    private final UserRepository users;

    public DeleteDriverCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<DeleteDriverCommand> messageType() { return DeleteDriverCommand.class; }

    @Transactional
    @Override
    public Void handle(DeleteDriverCommand command) {
        Long id = command.id();
        users.findByIdAndRole(id, Role.DRIVER).ifPresent(driver -> {
            if (driver.getVehicle() != null) driver.getVehicle().unassignDriver();
            users.delete(driver);
        });

        return null;
    }

}
