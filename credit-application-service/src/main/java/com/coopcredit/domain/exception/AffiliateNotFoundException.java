package com.coopcredit.domain.exception;

/**
 * Exception thrown when affiliate is not found.
 */
public class AffiliateNotFoundException extends DomainException {

    public AffiliateNotFoundException(String documentNumber) {
        super("Affiliate not found with document: " + documentNumber, "AFFILIATE_NOT_FOUND");
    }

    public AffiliateNotFoundException(Long id) {
        super("Affiliate not found with id: " + id, "AFFILIATE_NOT_FOUND");
    }
}
