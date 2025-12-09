package com.coopcredit.domain.exception;

/**
 * Exception thrown when insufficient affiliation time for credit.
 */
public class InsufficientAffiliationTimeException extends DomainException {

    public InsufficientAffiliationTimeException(long currentMonths, int requiredMonths) {
        super("Insufficient affiliation time. Current: " + currentMonths + " months, Required: " + requiredMonths
                + " months",
                "INSUFFICIENT_AFFILIATION_TIME");
    }
}
