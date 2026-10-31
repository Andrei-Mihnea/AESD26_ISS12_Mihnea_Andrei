package com.example.lab4.dto;

import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Map;

@JacksonXmlRootElement(localName = "customer")
public record CustomerResponse(Long id, String name, String email, String phone,
                               String address, String coordinates, Map<String, ResourceLink> _links) {
    public CustomerResponse {
        _links = Map.copyOf(_links);
    }
}
