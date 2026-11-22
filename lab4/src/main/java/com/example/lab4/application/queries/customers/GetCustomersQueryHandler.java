package com.example.lab4.application.queries.customers;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.lab4.application.cqrs.QueryHandler;

@Service
@Transactional(readOnly = true)
public class GetCustomersQueryHandler implements QueryHandler<GetCustomersQuery, List<User>> {
    private final UserRepository users;

    public GetCustomersQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override public Class<GetCustomersQuery> messageType() { return GetCustomersQuery.class; }

    @Override
    public List<User> handle(GetCustomersQuery query) {

        return users.findAllByRole(Role.CUSTOMER);
    }

}
