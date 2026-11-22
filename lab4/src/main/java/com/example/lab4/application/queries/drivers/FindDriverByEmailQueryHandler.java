package com.example.lab4.application.queries.drivers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class FindDriverByEmailQueryHandler implements QueryHandler<FindDriverByEmailQuery, Optional<User>> {
    private final UserRepository users;

    public FindDriverByEmailQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<FindDriverByEmailQuery> messageType() { return FindDriverByEmailQuery.class; }

    @Override
    public Optional<User> handle(FindDriverByEmailQuery query) {
        String email = query.email();
        return users.findByEmailAndRole(email, Role.DRIVER);
    }

}
