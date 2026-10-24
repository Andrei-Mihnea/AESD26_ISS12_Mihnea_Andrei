package com.example.lab4.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Optional filters for searching delivery requests. A null field is ignored. */
public record DeliverySearchCriteria(
        Long customerId,
        String areaContains,
        Integer quantityGreaterThan,
        Integer minQuantity,
        Integer maxQuantity,
        String notesContains,
        OffsetDateTime from,
        OffsetDateTime to,
        LocalDate registeredOn
) {
    public DeliverySearchCriteria {
        if (quantityGreaterThan != null && quantityGreaterThan < 0) {
            throw new IllegalArgumentException("quantityGreaterThan cannot be negative");
        }
        if (minQuantity != null && maxQuantity != null && minQuantity > maxQuantity) {
            throw new IllegalArgumentException("minQuantity cannot exceed maxQuantity");
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("from cannot be after to");
        }
    }
}
