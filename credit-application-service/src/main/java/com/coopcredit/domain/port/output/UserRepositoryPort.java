package com.coopcredit.domain.port.output;

import com.coopcredit.domain.model.User;
import java.util.Optional;

/**
 * Output port for user persistence.
 * This interface defines the operations for persisting users.
 */
public interface UserRepositoryPort {

    /**
     * Save a user.
     * 
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Find a user by username.
     * 
     * @param username the username
     * @return optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by ID.
     * 
     * @param id the user ID
     * @return optional containing the user if found
     */
    Optional<User> findById(Long id);

    /**
     * Check if a username exists.
     * 
     * @param username the username to check
     * @return true if the username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email exists.
     * 
     * @param email the email to check
     * @return true if the email exists
     */
    boolean existsByEmail(String email);
}
