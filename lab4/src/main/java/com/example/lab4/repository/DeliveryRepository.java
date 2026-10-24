package com.example.lab4.repository;

import com.example.lab4.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByCustomer_IdAndNotes(Long customerId, String notes);
}
