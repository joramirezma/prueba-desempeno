package com.coopcredit.application.service;

import com.coopcredit.domain.exception.DomainException;
import com.coopcredit.domain.exception.UserNotFoundException;
import com.coopcredit.domain.model.User;
import com.coopcredit.domain.model.enums.Role;
import com.coopcredit.domain.port.input.AuthUseCase;
import com.coopcredit.domain.port.output.UserRepositoryPort;
import com.coopcredit.infrastructure.config.MetricsService;
import com.coopcredit.infrastructure.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementing authentication use cases.
 */
@Service
@Transactional
public class AuthService implements AuthUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MetricsService metricsService;

    public AuthService(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            MetricsService metricsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.metricsService = metricsService;
    }

    @Override
    public User register(User user) {
        log.info("Registering new user: {}", user.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DomainException("Username already exists: " + user.getUsername(), "DUPLICATE_USERNAME");
        }

        // Check if email already exists
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new DomainException("Email already exists: " + user.getEmail(), "DUPLICATE_EMAIL");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.addRole(Role.ROLE_AFFILIATE);
        }

        // Enable user by default
        user.setEnabled(true);

        User saved = userRepository.save(user);
        log.info("User registered successfully: {}", saved.getUsername());
        return saved;
    }

    @Override
    public String login(String username, String password) {
        log.info("User login attempt: {}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = jwtTokenProvider.generateToken(authentication);
            metricsService.incrementLoginSuccess();
            log.info("User logged in successfully: {}", username);
            return token;

        } catch (AuthenticationException e) {
            metricsService.incrementLoginFailure();
            log.warn("Authentication failed for user: {}", username);
            throw new DomainException("Invalid username or password", "AUTHENTICATION_FAILED");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
