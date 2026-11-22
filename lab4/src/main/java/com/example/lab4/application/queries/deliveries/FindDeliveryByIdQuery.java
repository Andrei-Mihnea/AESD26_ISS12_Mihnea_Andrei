package com.example.lab4.application.queries.deliveries;

import com.example.lab4.domain.entities.Delivery;
import java.util.Optional;
import com.example.lab4.application.cqrs.Query;

public record FindDeliveryByIdQuery(Long id) implements Query<Optional<Delivery>> {}
