package com.example.lab4.application.queries.deliveries;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.domain.valueobjects.DeliverySearchCriteria;
import java.util.List;
import com.example.lab4.application.cqrs.Query;

public record SearchDeliveriesQuery(DeliverySearchCriteria criteria) implements Query<List<Delivery>> {}
