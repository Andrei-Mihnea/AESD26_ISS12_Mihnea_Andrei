package com.example.lab4.application.queries.users;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetUsersByRoleQueryHandler implements QueryHandler<GetUsersByRoleQuery, List<User>> {
    private final UserRepository users;

    public GetUsersByRoleQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<GetUsersByRoleQuery> messageType() { return GetUsersByRoleQuery.class; }

    @Override
    public List<User> handle(GetUsersByRoleQuery query) {
        Role role = query.role();
        return users.findAllByRole(role);
    }

}
