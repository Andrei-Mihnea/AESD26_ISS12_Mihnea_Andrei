package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.valueobjects.DeliverySearchCriteria;

import com.example.lab4.domain.entities.Delivery;

import java.util.List;

public interface IDeliverySearchRepository {
    List<Delivery> search(DeliverySearchCriteria criteria);
}
