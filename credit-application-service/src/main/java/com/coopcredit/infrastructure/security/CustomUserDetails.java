package com.coopcredit.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Custom UserDetails implementation that includes the document number
 * for affiliate access control.
 */
public class CustomUserDetails extends User {

    private final String documentNumber;

    public CustomUserDetails(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
            String documentNumber) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }
}
