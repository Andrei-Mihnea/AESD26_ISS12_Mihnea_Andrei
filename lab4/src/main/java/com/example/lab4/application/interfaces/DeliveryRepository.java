package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.Delivery;
import java.util.List;
import java.util.Optional;
import com.example.lab4.domain.valueobjects.DeliverySearchCriteria;

/** Persistence port implemented by the infrastructure JPA adapter. */
public interface DeliveryRepository {
    Optional<Delivery> findById(Long id);
    List<Delivery> findAll();
    <S extends Delivery> S save(S entity);
    <S extends Delivery> List<S> saveAll(Iterable<S> entities);
    void delete(Delivery entity);
    void deleteById(Long id);
    void flush();
    long count();
    List<Delivery> search(DeliverySearchCriteria criteria);
    List<Delivery> findByCustomer_IdOrderByIdAsc(Long customerId);
    boolean existsByCustomer_IdAndNotes(Long customerId, String notes);
}
