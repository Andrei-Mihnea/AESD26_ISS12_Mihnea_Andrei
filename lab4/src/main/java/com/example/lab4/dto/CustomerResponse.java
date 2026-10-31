package com.example.lab4.dto;

import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.example.lab4.entity.Customer;

@JacksonXmlRootElement(localName = "customer")
public record CustomerResponse(Long id, String name, String email, String phone,
                               String address, String coordinates) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(),
                customer.getPhone(), customer.getAddress(), customer.getCoordinates());
    }
}
