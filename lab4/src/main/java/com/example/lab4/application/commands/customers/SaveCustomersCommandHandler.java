package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class SaveCustomersCommandHandler implements CommandHandler<SaveCustomersCommand, List<User>> {
    private final UserRepository users;

    public SaveCustomersCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<SaveCustomersCommand> messageType() { return SaveCustomersCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public List<User> handle(SaveCustomersCommand command) {
        Iterable<User> customers = command.customers();
        List<User> selected = new ArrayList<>();
        customers.forEach(customer -> { customer.requireRole(Role.CUSTOMER); selected.add(customer); });
        return users.saveAll(selected);
    }

}
