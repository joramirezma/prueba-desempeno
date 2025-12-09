package com.coopcredit.domain.exception;

/**
 * Exception thrown when credit evaluation fails business rules.
 */
public class CreditEvaluationException extends DomainException {

    public CreditEvaluationException(String reason) {
        super("Credit evaluation failed: " + reason, "EVALUATION_FAILED");
    }
}
