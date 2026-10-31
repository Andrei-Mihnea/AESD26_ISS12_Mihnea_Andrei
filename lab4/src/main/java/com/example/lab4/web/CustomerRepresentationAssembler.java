package com.example.lab4.web;

import com.example.lab4.dto.CustomerResponse;
import com.example.lab4.dto.ResourceLink;
import com.example.lab4.entity.Customer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

/** Builds navigation links at the HTTP boundary, leaving entities independent of request URLs. */
@Component
public class CustomerRepresentationAssembler {
    public CustomerResponse toResponse(Customer customer) {
        String version = (String) RequestContextHolder.currentRequestAttributes()
                .getAttribute(ApiVersionInterceptor.VERSION_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String collection = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("/api/v" + (version == null ? "1" : version) + "/customers").build().toUriString();
        String self = collection + "/" + customer.getId();
        String deliveries = self + "/deliveries";
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone(),
                customer.getAddress(), customer.getCoordinates(), Map.of(
                        "self", new ResourceLink(self),
                        "customers", new ResourceLink(collection),
                        "deliveries", new ResourceLink(deliveries)));
    }
}
