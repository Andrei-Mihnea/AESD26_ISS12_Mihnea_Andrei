package com.example.lab4.application.queries.customers;

import com.example.lab4.domain.entities.User;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record FindCustomerByEmailQuery(String email) implements Query<Optional<User>> {}
