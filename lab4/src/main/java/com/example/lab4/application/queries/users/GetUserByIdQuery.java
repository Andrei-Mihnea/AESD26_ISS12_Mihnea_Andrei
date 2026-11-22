package com.example.lab4.application.queries.users;

import com.example.lab4.domain.entities.User;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record GetUserByIdQuery(Long id) implements Query<Optional<User>> {}
