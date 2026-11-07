package com.example.lab4.dto;

public record MockLoginResponse(String message, String authenticationScheme,
                                String csrfHeaderName, String csrfToken) {
}
