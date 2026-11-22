package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.dto.DeliveryResponse;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record GetCustomerDeliveriesQuery(Long customerId) implements Query<List<DeliveryResponse>> {}
