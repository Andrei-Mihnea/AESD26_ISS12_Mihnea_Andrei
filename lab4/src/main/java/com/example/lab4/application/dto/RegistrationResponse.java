package com.example.lab4.application.dto;

import com.example.lab4.domain.enums.Role;

public record RegistrationResponse(Long id, String username, Role role) {
}
