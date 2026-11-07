package com.example.lab4.web;

import com.example.lab4.dto.LoginRequest;
import com.example.lab4.dto.LoginResponse;
import com.example.lab4.dto.RefreshTokenRequest;
import com.example.lab4.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Authentication")
public class LoginController {
    private final LoginService login;
    public LoginController(LoginService login) { this.login = login; }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(summary = "Rotate a local refresh token", description = "Returns a fresh access/refresh pair. Each refresh token is single-use. Reuse revokes the entire session family; invalid/expired/revoked tokens return 401. Keycloak tokens must be refreshed at Keycloak's token endpoint.")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) { return login.refresh(request.refreshToken()); }

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirements
    @Operation(summary = "Revoke a local refresh session", description = "Revokes the refresh-token family. Existing access JWTs remain valid until expiration. Unknown refresh tokens are ignored.")
    public void logout(@Valid @RequestBody RefreshTokenRequest request) { login.logout(request.refreshToken()); }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(summary = "Authenticate and issue a JWT", description = "Checks the username, BCrypt password, "
            + "and enabled flag in the database. Returns an access JWT and a single-use refresh token. "
            + "Invalid credentials or disabled accounts return 401; invalid request bodies return 400.")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) { return login.login(request); }

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(summary = "JWT login instructions")
    public Map<String, String> info() {
        return Map.of("message", "POST username and password as JSON to /login. Send the returned token as Authorization: Bearer <token>.",
                "authenticationScheme", "Bearer");
    }

}
