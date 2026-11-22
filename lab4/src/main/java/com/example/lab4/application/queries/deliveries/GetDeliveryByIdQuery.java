package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.dto.DeliveryResponse;
import com.example.lab4.application.cqrs.Query;

public record GetDeliveryByIdQuery(Long id) implements Query<DeliveryResponse> {}
