package com.example.lab4.application.queries.customers;

import com.example.lab4.domain.entities.User;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetCustomersQuery() implements Query<List<User>> {}
