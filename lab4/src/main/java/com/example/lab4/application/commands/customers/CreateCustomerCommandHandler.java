package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.dto.CustomerRequest;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class CreateCustomerCommandHandler implements CommandHandler<CreateCustomerCommand, User> {
    private final UserRepository users;

    public CreateCustomerCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<CreateCustomerCommand> messageType() { return CreateCustomerCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public User handle(CreateCustomerCommand command) {
        CustomerRequest request = command.request();
        return users.save(User.customer(request.name(), request.email(), request.phone(), request.address(), request.coordinates()));
    }

}
