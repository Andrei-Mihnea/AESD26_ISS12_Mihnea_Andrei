package com.example.lab4.application.commands.users;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class CreateAdministratorCommandHandler implements CommandHandler<CreateAdministratorCommand, User> {
    private final UserRepository users;

    public CreateAdministratorCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<CreateAdministratorCommand> messageType() { return CreateAdministratorCommand.class; }

    @Transactional
    @Override
    public User handle(CreateAdministratorCommand command) {
        String name = command.name();
        String email = command.email();
        String phone = command.phone();
        return users.save(new User(name, email, phone));
    }

}
