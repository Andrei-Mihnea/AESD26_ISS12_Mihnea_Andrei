package com.example.lab4.application.queries.drivers;

import com.example.lab4.domain.entities.User;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetDriversQuery() implements Query<List<User>> {}
