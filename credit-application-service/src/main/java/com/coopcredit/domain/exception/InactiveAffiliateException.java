package com.coopcredit.domain.exception;

/**
 * Exception thrown when affiliate is inactive and cannot apply for credit.
 */
public class InactiveAffiliateException extends DomainException {

    public InactiveAffiliateException(String documentNumber) {
        super("Affiliate is inactive and cannot apply for credit: " + documentNumber, "INACTIVE_AFFILIATE");
    }
}
