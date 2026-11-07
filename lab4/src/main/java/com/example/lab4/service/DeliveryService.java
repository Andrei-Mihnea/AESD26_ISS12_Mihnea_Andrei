package com.example.lab4.service;

import com.example.lab4.exception.ResourceNotFoundException;

import com.example.lab4.entity.User;
import com.example.lab4.entity.Delivery;
import com.example.lab4.repository.UserRepository;
import com.example.lab4.entity.Role;
import com.example.lab4.repository.DeliverySearchCriteria;
import com.example.lab4.repository.IDeliveryRepository;
import com.example.lab4.dto.DeliveryRequest;
import com.example.lab4.dto.DeliveryResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final IDeliveryRepository deliveryRepository;
    private final UserRepository customers;

    public DeliveryService(IDeliveryRepository deliveryRepository, UserRepository customers) {
        this.deliveryRepository = deliveryRepository;
        this.customers = customers;
    }

    public List<Delivery> findAll() { return deliveryRepository.findAll(); }
    public Optional<Delivery> findById(Long id) { return deliveryRepository.findById(id); }
    public List<Delivery> search(DeliverySearchCriteria criteria) { return deliveryRepository.search(criteria); }
    public boolean existsForCustomerWithNotes(Long customerId, String notes) {
        return deliveryRepository.existsByCustomer_IdAndNotes(customerId, notes);
    }

    @Transactional
    public Delivery save(Delivery delivery) { return deliveryRepository.save(delivery); }

    @Transactional
    public List<Delivery> saveAll(Iterable<Delivery> deliveries) { return deliveryRepository.saveAll(deliveries); }

    @Transactional
    public void deleteById(Long id) { deliveryRepository.deleteById(id); }

    private Delivery require(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Delivery", id));
    }

    public List<DeliveryResponse> findAllResponses() {
        return deliveryRepository.findAll().stream().map(DeliveryResponse::from).toList();
    }

    public List<DeliveryResponse> findForCustomer(Long customerId) {
        customer(customerId);
        return deliveryRepository.findByCustomer_IdOrderByIdAsc(customerId).stream()
                .map(DeliveryResponse::from).toList();
    }

    public DeliveryResponse findResponse(Long id) { return DeliveryResponse.from(require(id)); }

    @Transactional
    public void deleteExisting(Long id) {
        deliveryRepository.delete(require(id));
        deliveryRepository.flush();
    }

    private User customer(Long id) {
        return customers.findByIdAndRole(id, Role.CUSTOMER).orElseThrow(() ->
                new ResourceNotFoundException("Customer", id));
    }

    @Transactional
    public DeliveryResponse create(DeliveryRequest request) {
        return DeliveryResponse.from(deliveryRepository.save(new Delivery(customer(request.customerId()),
                request.quantity(), request.notes(), request.timestamp())));
    }

    @Transactional
    public DeliveryResponse update(Long id, DeliveryRequest request) {
        Delivery delivery = require(id);
        delivery.updateDetails(customer(request.customerId()), request.quantity(), request.notes(), request.timestamp());
        return DeliveryResponse.from(delivery);
    }
}
