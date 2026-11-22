package com.example.lab4.application.queries.users;

import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetUsersByRoleQuery(Role role) implements Query<List<User>> {}
