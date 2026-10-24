package com.example.lab4.repository;

import com.example.lab4.entity.Customer;
import com.example.lab4.entity.Delivery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class IDeliverySearchRepositoryImpl implements IDeliverySearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Delivery> search(DeliverySearchCriteria criteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Delivery> query = builder.createQuery(Delivery.class);
        Root<Delivery> delivery = query.from(Delivery.class);
        Join<Delivery, Customer> customer = delivery.join("customer", JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.customerId() != null) {
            predicates.add(builder.equal(customer.get("id"), criteria.customerId()));
        }
        if (criteria.areaContains() != null && !criteria.areaContains().isBlank()) {
            String areaPattern = likePattern(criteria.areaContains());
            predicates.add(builder.or(
                    builder.like(builder.lower(customer.<String>get("address")), areaPattern, '\\'),
                    builder.like(builder.lower(customer.<String>get("coordinates")), areaPattern, '\\')));
        }
        if (criteria.quantityGreaterThan() != null) {
            predicates.add(builder.greaterThan(delivery.<Integer>get("quantity"), criteria.quantityGreaterThan()));
        }
        if (criteria.minQuantity() != null) {
            predicates.add(builder.greaterThanOrEqualTo(delivery.<Integer>get("quantity"), criteria.minQuantity()));
        }
        if (criteria.maxQuantity() != null) {
            predicates.add(builder.lessThanOrEqualTo(delivery.<Integer>get("quantity"), criteria.maxQuantity()));
        }
        if (criteria.notesContains() != null && !criteria.notesContains().isBlank()) {
            predicates.add(builder.like(builder.lower(delivery.<String>get("notes")),
                    likePattern(criteria.notesContains()), '\\'));
        }
        if (criteria.from() != null) {
            predicates.add(builder.greaterThanOrEqualTo(delivery.<OffsetDateTime>get("timestamp"), criteria.from()));
        }
        if (criteria.to() != null) {
            predicates.add(builder.lessThanOrEqualTo(delivery.<OffsetDateTime>get("timestamp"), criteria.to()));
        }
        if (criteria.registeredOn() != null) {
            OffsetDateTime startOfDate = criteria.registeredOn().atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime startOfNextDate = criteria.registeredOn().plusDays(1)
                    .atStartOfDay().atOffset(ZoneOffset.UTC);
            predicates.add(builder.greaterThanOrEqualTo(delivery.<OffsetDateTime>get("timestamp"), startOfDate));
            predicates.add(builder.lessThan(delivery.<OffsetDateTime>get("timestamp"), startOfNextDate));
        }

        query.select(delivery)
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(builder.desc(delivery.get("timestamp")), builder.asc(delivery.get("id")));

        TypedQuery<Delivery> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    private String likePattern(String value) {
        String escaped = value.toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
