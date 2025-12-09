package com.coopcredit.domain.exception;

/**
 * Exception thrown when user is not found.
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String username) {
        super("User not found: " + username, "USER_NOT_FOUND");
    }
}
