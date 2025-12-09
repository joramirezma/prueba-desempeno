package com.coopcredit.domain.port.input;

import com.coopcredit.domain.model.Affiliate;
import java.util.List;
import java.util.Optional;

/**
 * Input port for affiliate management use cases.
 * This interface defines the operations that can be performed on affiliates.
 */
public interface AffiliateUseCase {

    /**
     * Register a new affiliate.
     * 
     * @param affiliate the affiliate to register
     * @return the registered affiliate with generated ID
     */
    Affiliate register(Affiliate affiliate);

    /**
     * Update an existing affiliate's information.
     * 
     * @param documentNumber the document number of the affiliate to update
     * @param affiliate      the updated affiliate data
     * @return the updated affiliate
     */
    Affiliate update(String documentNumber, Affiliate affiliate);

    /**
     * Find an affiliate by document number.
     * 
     * @param documentNumber the document number to search
     * @return optional containing the affiliate if found
     */
    Optional<Affiliate> findByDocumentNumber(String documentNumber);

    /**
     * Find an affiliate by ID.
     * 
     * @param id the affiliate ID
     * @return optional containing the affiliate if found
     */
    Optional<Affiliate> findById(Long id);

    /**
     * Get all affiliates.
     * 
     * @return list of all affiliates
     */
    List<Affiliate> findAll();

    /**
     * Activate an affiliate.
     * 
     * @param documentNumber the document number of the affiliate
     * @return the activated affiliate
     */
    Affiliate activate(String documentNumber);

    /**
     * Deactivate an affiliate.
     * 
     * @param documentNumber the document number of the affiliate
     * @return the deactivated affiliate
     */
    Affiliate deactivate(String documentNumber);
}
