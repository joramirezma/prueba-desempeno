package com.coopcredit.infrastructure.adapter.input.rest;

import com.coopcredit.application.dto.*;
import com.coopcredit.domain.model.User;
import com.coopcredit.domain.model.enums.Role;
import com.coopcredit.domain.port.input.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for user: {}", request.username());

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setEmail(request.email());
        user.setDocumentNumber(request.documentNumber());

        // Set roles
        Set<Role> roles = request.roles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.ROLE_AFFILIATE);
        }
        user.setRoles(roles);

        User registeredUser = authUseCase.register(user);

        // Generate token for the new user
        String token = authUseCase.login(request.username(), request.password());

        AuthResponse response = new AuthResponse(token, registeredUser.getUsername(), registeredUser.getRoles());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.username());

        String token = authUseCase.login(request.username(), request.password());
        User user = authUseCase.findByUsername(request.username());

        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRoles());
        return ResponseEntity.ok(response);
    }
}
