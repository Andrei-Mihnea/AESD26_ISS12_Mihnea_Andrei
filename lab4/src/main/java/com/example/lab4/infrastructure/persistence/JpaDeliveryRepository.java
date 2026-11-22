package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.application.interfaces.DeliveryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryRepository, IDeliverySearchRepository {
    java.util.List<Delivery> findByCustomer_IdOrderByIdAsc(Long customerId);
    boolean existsByCustomer_IdAndNotes(Long customerId, String notes);
}
