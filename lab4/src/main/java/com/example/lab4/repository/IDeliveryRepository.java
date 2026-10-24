package com.example.lab4.repository;

import com.example.lab4.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDeliveryRepository extends JpaRepository<Delivery, Long>, IDeliverySearchRepository {
    boolean existsByCustomer_IdAndNotes(Long customerId, String notes);
}
