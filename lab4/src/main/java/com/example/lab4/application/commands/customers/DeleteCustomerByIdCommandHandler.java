package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteCustomerByIdCommandHandler implements CommandHandler<DeleteCustomerByIdCommand, Void> {
    private final UserRepository users;

    public DeleteCustomerByIdCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<DeleteCustomerByIdCommand> messageType() { return DeleteCustomerByIdCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public Void handle(DeleteCustomerByIdCommand command) {
        Long id = command.id();
        users.findByIdAndRole(id, Role.CUSTOMER).ifPresent(users::delete);

        return null;
    }

}
