package com.example.lab4.application.queries.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindCustomerByEmailQueryHandler implements QueryHandler<FindCustomerByEmailQuery, Optional<User>> {
    private final UserRepository users;

    public FindCustomerByEmailQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<FindCustomerByEmailQuery> messageType() { return FindCustomerByEmailQuery.class; }

    @Cacheable(cacheNames = "customersByEmail", key = "#query.email()")
    @Override
    public Optional<User> handle(FindCustomerByEmailQuery query) {
        String email = query.email();
        return users.findByEmailAndRole(email, Role.CUSTOMER);
    }

}
