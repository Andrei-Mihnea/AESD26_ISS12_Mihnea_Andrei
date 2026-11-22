package com.example.lab4.application.queries.drivers;

import com.example.lab4.domain.entities.User;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record FindDriverByEmailQuery(String email) implements Query<Optional<User>> {}
