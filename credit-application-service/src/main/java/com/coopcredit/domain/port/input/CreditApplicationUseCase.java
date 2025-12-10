package com.coopcredit.domain.port.input;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.enums.ApplicationStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Input port for credit application use cases.
 * This interface defines the operations for managing credit applications.
 */
public interface CreditApplicationUseCase {

    /**
     * Create a new credit application.
     * 
     * @param affiliateDocumentNumber the document number of the affiliate
     * @param requestedAmount         the amount requested
     * @param termMonths              the term in months
     * @param proposedRate            the proposed interest rate
     * @return the created credit application
     */
    CreditApplication create(String affiliateDocumentNumber, BigDecimal requestedAmount,
            Integer termMonths, BigDecimal proposedRate);

    /**
     * Evaluate risk for a pending credit application (automatic).
     * This calculates metrics and consults external risk service.
     * 
     * @param applicationId the ID of the application to evaluate
     * @return the application with risk evaluation data
     */
    CreditApplication evaluateRisk(Long applicationId);

    /**
     * Manually approve or reject an application (analyst decision).
     * 
     * @param applicationId the ID of the application
     * @param approved true to approve, false to reject
     * @param comments analyst comments
     * @return the updated application
     */
    CreditApplication makeDecision(Long applicationId, boolean approved, String comments);

    /**
     * Find a credit application by ID.
     * 
     * @param id the application ID
     * @return optional containing the application if found
     */
    Optional<CreditApplication> findById(Long id);

    /**
     * Get all credit applications for an affiliate.
     * 
     * @param affiliateDocumentNumber the document number of the affiliate
     * @return list of credit applications for the affiliate
     */
    List<CreditApplication> findByAffiliateDocument(String affiliateDocumentNumber);

    /**
     * Get all credit applications with a specific status.
     * 
     * @param status the application status
     * @return list of credit applications with the given status
     */
    List<CreditApplication> findByStatus(ApplicationStatus status);

    /**
     * Get all pending credit applications (for analysts).
     * 
     * @return list of pending credit applications
     */
    List<CreditApplication> findPendingApplications();

    /**
     * Get all credit applications.
     * 
     * @return list of all credit applications
     */
    List<CreditApplication> findAll();
}
