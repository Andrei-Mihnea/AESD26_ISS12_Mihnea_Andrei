package com.example.lab4.application.queries.deliveries;

import com.example.lab4.application.cqrs.Query;

public record DeliveryExistsForCustomerWithNotesQuery(Long customerId, String notes) implements Query<Boolean> {}
