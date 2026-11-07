package com.example.lab4.dto;

import com.example.lab4.entity.Role;

public record RegistrationResponse(Long id, String username, Role role) {
}
