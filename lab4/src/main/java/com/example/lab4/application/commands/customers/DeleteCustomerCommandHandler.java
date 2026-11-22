package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class DeleteCustomerCommandHandler implements CommandHandler<DeleteCustomerCommand, Boolean> {
    private final UserRepository users;

    public DeleteCustomerCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<DeleteCustomerCommand> messageType() { return DeleteCustomerCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public Boolean handle(DeleteCustomerCommand command) {
        Long id = command.id();
        return users.findByIdAndRole(id, Role.CUSTOMER).map(customer -> {
            users.delete(customer);
            users.flush();
            return true;
        }).orElse(false);
    }

}
