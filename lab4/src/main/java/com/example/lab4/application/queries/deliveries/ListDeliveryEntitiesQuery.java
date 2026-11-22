package com.example.lab4.application.queries.deliveries;

import com.example.lab4.domain.entities.Delivery;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record ListDeliveryEntitiesQuery() implements Query<List<Delivery>> {}
