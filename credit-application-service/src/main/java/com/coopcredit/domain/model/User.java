package com.coopcredit.domain.model;

import com.coopcredit.domain.model.enums.Role;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain model representing a user in the system.
 * This is a pure domain object with no framework dependencies.
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String documentNumber; // Links to affiliate if ROLE_AFFILIATE
    private Set<Role> roles;
    private Boolean enabled;

    public User() {
        this.roles = new HashSet<>();
        this.enabled = true;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = new HashSet<>();
        this.enabled = true;
    }

    // Domain behavior methods

    /**
     * Check if user has admin role.
     */
    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    /**
     * Check if user has analyst role.
     */
    public boolean isAnalyst() {
        return roles.contains(Role.ROLE_ANALYST);
    }

    /**
     * Check if user has affiliate role.
     */
    public boolean isAffiliate() {
        return roles.contains(Role.ROLE_AFFILIATE);
    }

    /**
     * Check if user has a specific role.
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    /**
     * Add a role to the user.
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Remove a role from the user.
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
