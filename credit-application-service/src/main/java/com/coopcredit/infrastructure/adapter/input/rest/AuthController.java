package com.coopcredit.infrastructure.adapter.input.rest;

import com.coopcredit.application.dto.*;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.User;
import com.coopcredit.domain.model.enums.AffiliateStatus;
import com.coopcredit.domain.model.enums.Role;
import com.coopcredit.domain.port.input.AffiliateUseCase;
import com.coopcredit.domain.port.input.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    private final AffiliateUseCase affiliateUseCase;

    public AuthController(AuthUseCase authUseCase, AffiliateUseCase affiliateUseCase) {
        this.authUseCase = authUseCase;
        this.affiliateUseCase = affiliateUseCase;
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

        // If user is an AFFILIATE, create the affiliate record with salary
        if (roles.contains(Role.ROLE_AFFILIATE) && request.documentNumber() != null && request.salary() != null) {
            Affiliate affiliate = new Affiliate();
            affiliate.setDocumentNumber(request.documentNumber());
            affiliate.setName(request.name() != null ? request.name() : request.username());
            affiliate.setSalary(request.salary());
            affiliate.setAffiliationDate(LocalDate.now());
            affiliate.setStatus(AffiliateStatus.ACTIVE);
            
            affiliateUseCase.register(affiliate);
            log.info("Affiliate record created for user: {}", request.username());
        }

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
