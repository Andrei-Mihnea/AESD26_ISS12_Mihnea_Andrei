package com.example.lab4.service;

import com.example.lab4.entity.Delivery;
import com.example.lab4.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public List<Delivery> findAll() { return deliveryRepository.findAll(); }
    public Optional<Delivery> findById(Long id) { return deliveryRepository.findById(id); }
    public boolean existsForCustomerWithNotes(Long customerId, String notes) {
        return deliveryRepository.existsByCustomer_IdAndNotes(customerId, notes);
    }

    @Transactional
    public Delivery save(Delivery delivery) { return deliveryRepository.save(delivery); }

    @Transactional
    public void deleteById(Long id) { deliveryRepository.deleteById(id); }
}
