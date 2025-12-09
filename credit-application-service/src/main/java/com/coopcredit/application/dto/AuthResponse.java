package com.coopcredit.application.dto;

import com.coopcredit.domain.model.enums.Role;
import java.util.Set;

/**
 * DTO for authentication response.
 */
public record AuthResponse(
        String token,
        String type,
        String username,
        Set<Role> roles) {
    public AuthResponse(String token, String username, Set<Role> roles) {
        this(token, "Bearer", username, roles);
    }
}
