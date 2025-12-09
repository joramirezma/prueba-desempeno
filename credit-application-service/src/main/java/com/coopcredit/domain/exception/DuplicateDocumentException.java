package com.coopcredit.domain.exception;

/**
 * Exception thrown when a duplicate document number is detected.
 */
public class DuplicateDocumentException extends DomainException {

    public DuplicateDocumentException(String documentNumber) {
        super("Document number already exists: " + documentNumber, "DUPLICATE_DOCUMENT");
    }
}
