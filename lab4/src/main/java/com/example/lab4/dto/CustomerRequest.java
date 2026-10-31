package com.example.lab4.dto;

import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JacksonXmlRootElement(localName = "customer")
public record CustomerRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 40) String phone,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 255) String coordinates) {
}
