package com.example.lab4.application.commands.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.CommandHandler;

@Service
@Transactional
public class UpdateCustomerCommandHandler implements CommandHandler<UpdateCustomerCommand, Optional<User>> {
    private final UserRepository users;

    public UpdateCustomerCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<UpdateCustomerCommand> messageType() { return UpdateCustomerCommand.class; }

    @Transactional
    @CacheEvict(cacheNames = "customersByEmail", allEntries = true)
    @Override
    public Optional<User> handle(UpdateCustomerCommand command) {
        Long id = command.id();
        String name = command.name();
        String email = command.email();
        String phone = command.phone();
        String address = command.address();
        String coordinates = command.coordinates();
        return users.findByIdAndRole(id, Role.CUSTOMER).map(customer -> {
            customer.updateCustomerDetails(name, email, phone, address, coordinates);
            return customer;
        });
    }

}
