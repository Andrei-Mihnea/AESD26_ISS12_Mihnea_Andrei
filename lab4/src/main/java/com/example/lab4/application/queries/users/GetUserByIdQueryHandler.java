package com.example.lab4.application.queries.users;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetUserByIdQueryHandler implements QueryHandler<GetUserByIdQuery, Optional<User>> {
    private final UserRepository users;

    public GetUserByIdQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<GetUserByIdQuery> messageType() { return GetUserByIdQuery.class; }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        Long id = query.id();
        return users.findById(id);
    }

}
