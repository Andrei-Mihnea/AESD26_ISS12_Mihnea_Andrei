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
public class FindDriverByIdQueryHandler implements QueryHandler<FindDriverByIdQuery, Optional<User>> {
    private final UserRepository users;

    public FindDriverByIdQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<FindDriverByIdQuery> messageType() { return FindDriverByIdQuery.class; }

    @Override
    public Optional<User> handle(FindDriverByIdQuery query) {
        Long id = query.id();
        return users.findByIdAndRole(id, Role.DRIVER);
    }

}
