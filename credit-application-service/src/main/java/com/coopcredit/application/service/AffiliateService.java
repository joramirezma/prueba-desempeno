package com.coopcredit.application.service;

import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.exception.DuplicateDocumentException;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.enums.AffiliateStatus;
import com.coopcredit.domain.port.input.AffiliateUseCase;
import com.coopcredit.domain.port.output.AffiliateRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing affiliate use cases.
 */
@Service
@Transactional
public class AffiliateService implements AffiliateUseCase {

    private static final Logger log = LoggerFactory.getLogger(AffiliateService.class);

    private final AffiliateRepositoryPort affiliateRepository;

    public AffiliateService(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate register(Affiliate affiliate) {
        log.info("Registering new affiliate with document: {}", affiliate.getDocumentNumber());

        // Validate unique document number
        if (affiliateRepository.existsByDocumentNumber(affiliate.getDocumentNumber())) {
            throw new DuplicateDocumentException(affiliate.getDocumentNumber());
        }

        // Set default status if not provided
        if (affiliate.getStatus() == null) {
            affiliate.setStatus(AffiliateStatus.ACTIVE);
        }

        Affiliate saved = affiliateRepository.save(affiliate);
        log.info("Affiliate registered successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Affiliate update(String documentNumber, Affiliate updatedData) {
        log.info("Updating affiliate with document: {}", documentNumber);

        Affiliate existing = affiliateRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(documentNumber));

        // Update allowed fields
        if (updatedData.getName() != null) {
            existing.setName(updatedData.getName());
        }
        if (updatedData.getSalary() != null) {
            existing.setSalary(updatedData.getSalary());
        }
        if (updatedData.getStatus() != null) {
            existing.setStatus(updatedData.getStatus());
        }

        Affiliate saved = affiliateRepository.save(existing);
        log.info("Affiliate updated successfully: {}", documentNumber);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Affiliate> findByDocumentNumber(String documentNumber) {
        log.debug("Finding affiliate by document: {}", documentNumber);
        return affiliateRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Affiliate> findById(Long id) {
        log.debug("Finding affiliate by ID: {}", id);
        return affiliateRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Affiliate> findAll() {
        log.debug("Finding all affiliates");
        return affiliateRepository.findAll();
    }

    @Override
    public Affiliate activate(String documentNumber) {
        log.info("Activating affiliate: {}", documentNumber);

        Affiliate affiliate = affiliateRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(documentNumber));

        affiliate.setStatus(AffiliateStatus.ACTIVE);
        return affiliateRepository.save(affiliate);
    }

    @Override
    public Affiliate deactivate(String documentNumber) {
        log.info("Deactivating affiliate: {}", documentNumber);

        Affiliate affiliate = affiliateRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(documentNumber));

        affiliate.setStatus(AffiliateStatus.INACTIVE);
        return affiliateRepository.save(affiliate);
    }
}
