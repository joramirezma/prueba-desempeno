package com.coopcredit.domain.exception;

/**
 * Exception thrown when credit application is not found.
 */
public class CreditApplicationNotFoundException extends DomainException {

    public CreditApplicationNotFoundException(Long id) {
        super("Credit application not found with id: " + id, "APPLICATION_NOT_FOUND");
    }
}
