package com.example.lab4.application.queries.drivers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetDriversQueryHandler implements QueryHandler<GetDriversQuery, List<User>> {
    private final UserRepository users;

    public GetDriversQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<GetDriversQuery> messageType() { return GetDriversQuery.class; }

    @Override
    public List<User> handle(GetDriversQuery query) {

        return users.findAllByRole(Role.DRIVER);
    }

}
