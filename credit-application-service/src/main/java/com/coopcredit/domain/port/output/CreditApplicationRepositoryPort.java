package com.coopcredit.domain.port.output;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.enums.ApplicationStatus;
import java.util.List;
import java.util.Optional;

/**
 * Output port for credit application persistence.
 * This interface defines the operations for persisting credit applications.
 */
public interface CreditApplicationRepositoryPort {

    /**
     * Save a credit application.
     * 
     * @param application the application to save
     * @return the saved application
     */
    CreditApplication save(CreditApplication application);

    /**
     * Find a credit application by ID.
     * 
     * @param id the application ID
     * @return optional containing the application if found
     */
    Optional<CreditApplication> findById(Long id);

    /**
     * Find a credit application by ID with affiliate data eagerly loaded.
     * 
     * @param id the application ID
     * @return optional containing the application if found
     */
    Optional<CreditApplication> findByIdWithAffiliate(Long id);

    /**
     * Get all credit applications for an affiliate.
     * 
     * @param affiliateId the affiliate ID
     * @return list of credit applications
     */
    List<CreditApplication> findByAffiliateId(Long affiliateId);

    /**
     * Get all credit applications for an affiliate by document number.
     * 
     * @param documentNumber the affiliate's document number
     * @return list of credit applications
     */
    List<CreditApplication> findByAffiliateDocumentNumber(String documentNumber);

    /**
     * Get all credit applications with a specific status.
     * 
     * @param status the application status
     * @return list of credit applications
     */
    List<CreditApplication> findByStatus(ApplicationStatus status);

    /**
     * Get all credit applications.
     * 
     * @return list of all credit applications
     */
    List<CreditApplication> findAll();

    /**
     * Delete a credit application by ID.
     * 
     * @param id the application ID
     */
    void deleteById(Long id);
}
