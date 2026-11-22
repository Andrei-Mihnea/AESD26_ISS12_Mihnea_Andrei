package com.example.lab4.application.queries.users;

import com.example.lab4.domain.entities.User;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetUsersQuery() implements Query<List<User>> {}
