package com.example.lab4.repository;

import com.example.lab4.entity.Delivery;

import java.util.List;

public interface IDeliverySearchRepository {
    List<Delivery> search(DeliverySearchCriteria criteria);
}
