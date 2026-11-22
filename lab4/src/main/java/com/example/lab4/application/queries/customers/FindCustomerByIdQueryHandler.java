package com.example.lab4.application.queries.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindCustomerByIdQueryHandler implements QueryHandler<FindCustomerByIdQuery, Optional<User>> {
    private final UserRepository users;

    public FindCustomerByIdQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<FindCustomerByIdQuery> messageType() { return FindCustomerByIdQuery.class; }

    @Override
    public Optional<User> handle(FindCustomerByIdQuery query) {
        Long id = query.id();
        return users.findByIdAndRole(id, Role.CUSTOMER);
    }

}
