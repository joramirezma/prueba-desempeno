package com.coopcredit.domain.port.input;

import com.coopcredit.domain.model.User;

/**
 * Input port for authentication use cases.
 * This interface defines the operations for user authentication and
 * registration.
 */
public interface AuthUseCase {

    /**
     * Register a new user.
     * 
     * @param user the user to register
     * @return the registered user
     */
    User register(User user);

    /**
     * Authenticate a user and generate a JWT token.
     * 
     * @param username the username
     * @param password the password
     * @return the JWT token if authentication is successful
     */
    String login(String username, String password);

    /**
     * Find a user by username.
     * 
     * @param username the username to search
     * @return the user if found
     */
    User findByUsername(String username);

    /**
     * Check if a username exists.
     * 
     * @param username the username to check
     * @return true if the username exists
     */
    boolean existsByUsername(String username);
}
