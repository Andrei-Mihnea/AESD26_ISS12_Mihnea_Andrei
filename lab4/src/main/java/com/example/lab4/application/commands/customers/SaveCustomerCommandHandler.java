package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveCustomerCommandHandler implements CommandHandler<SaveCustomerCommand, User> {
    private final UserRepository users;

    public SaveCustomerCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<SaveCustomerCommand> messageType() { return SaveCustomerCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public User handle(SaveCustomerCommand command) {
        User customer = command.customer();
        customer.requireRole(Role.CUSTOMER);
        return users.save(customer);
    }

}
