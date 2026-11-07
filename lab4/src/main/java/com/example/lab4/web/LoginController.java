package com.example.lab4.web;

import com.example.lab4.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DeferredCsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication")
public class LoginController {
    @GetMapping(value = "/login", produces = "application/json")
    @SecurityRequirements
    @Operation(summary = "Public mock login endpoint", description = "Does not sign in or issue an access token. "
            + "Use HTTP Basic credentials on protected requests. This endpoint also sets the XSRF-TOKEN cookie "
            + "and returns the matching CSRF token/header required for POST, PUT, and DELETE.")
    public MockLoginResponse login(HttpServletRequest request) {
        // SPA clients submit the raw cookie token in a header, not the masked view token.
        CsrfToken csrfToken = ((DeferredCsrfToken) request.getAttribute(DeferredCsrfToken.class.getName())).get();
        return new MockLoginResponse("Mock login: send HTTP Basic credentials with each protected request.",
                "Basic", csrfToken.getHeaderName(), csrfToken.getToken());
    }
}
