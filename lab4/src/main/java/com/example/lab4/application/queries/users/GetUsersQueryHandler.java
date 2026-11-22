package com.example.lab4.application.queries.users;

import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetUsersQueryHandler implements QueryHandler<GetUsersQuery, List<User>> {
    private final UserRepository users;

    public GetUsersQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<GetUsersQuery> messageType() { return GetUsersQuery.class; }

    @Override
    public List<User> handle(GetUsersQuery query) {

        return users.findAll();
    }

}
