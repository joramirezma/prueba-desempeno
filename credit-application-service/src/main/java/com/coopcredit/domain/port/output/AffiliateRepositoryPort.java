package com.coopcredit.domain.port.output;

import com.coopcredit.domain.model.Affiliate;
import java.util.List;
import java.util.Optional;

/**
 * Output port for affiliate persistence.
 * This interface defines the operations for persisting affiliates.
 */
public interface AffiliateRepositoryPort {

    /**
     * Save an affiliate.
     * 
     * @param affiliate the affiliate to save
     * @return the saved affiliate
     */
    Affiliate save(Affiliate affiliate);

    /**
     * Find an affiliate by ID.
     * 
     * @param id the affiliate ID
     * @return optional containing the affiliate if found
     */
    Optional<Affiliate> findById(Long id);

    /**
     * Find an affiliate by document number.
     * 
     * @param documentNumber the document number
     * @return optional containing the affiliate if found
     */
    Optional<Affiliate> findByDocumentNumber(String documentNumber);

    /**
     * Get all affiliates.
     * 
     * @return list of all affiliates
     */
    List<Affiliate> findAll();

    /**
     * Check if a document number exists.
     * 
     * @param documentNumber the document number to check
     * @return true if the document number exists
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Delete an affiliate by ID.
     * 
     * @param id the affiliate ID
     */
    void deleteById(Long id);
}
