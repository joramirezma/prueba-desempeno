package com.coopcredit.infrastructure.adapter.output.persistence;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.enums.ApplicationStatus;
import com.coopcredit.domain.port.output.CreditApplicationRepositoryPort;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.RiskEvaluationEntity;
import com.coopcredit.infrastructure.adapter.output.persistence.mapper.CreditApplicationMapper;
import com.coopcredit.infrastructure.adapter.output.persistence.repository.JpaAffiliateRepository;
import com.coopcredit.infrastructure.adapter.output.persistence.repository.JpaCreditApplicationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the CreditApplicationRepositoryPort.
 */
@Component
public class CreditApplicationRepositoryAdapter implements CreditApplicationRepositoryPort {

    private final JpaCreditApplicationRepository jpaRepository;
    private final JpaAffiliateRepository affiliateRepository;
    private final CreditApplicationMapper mapper;

    public CreditApplicationRepositoryAdapter(
            JpaCreditApplicationRepository jpaRepository,
            JpaAffiliateRepository affiliateRepository,
            CreditApplicationMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.affiliateRepository = affiliateRepository;
        this.mapper = mapper;
    }

    @Override
    public CreditApplication save(CreditApplication application) {
        CreditApplicationEntity entity;

        if (application.getId() != null) {
            // Update existing
            entity = jpaRepository.findById(application.getId())
                    .orElseThrow(() -> new RuntimeException("Application not found: " + application.getId()));

            entity.setRequestedAmount(application.getRequestedAmount());
            entity.setTermMonths(application.getTermMonths());
            entity.setProposedRate(application.getProposedRate());
            entity.setApplicationDate(application.getApplicationDate());
            entity.setStatus(application.getStatus());

            // Handle risk evaluation
            if (application.getRiskEvaluation() != null) {
                RiskEvaluationEntity riskEntity = mapper.riskEvaluationToEntity(application.getRiskEvaluation());
                riskEntity.setCreditApplication(entity);
                entity.setRiskEvaluation(riskEntity);
            }
        } else {
            // Create new
            entity = mapper.toEntity(application);

            // Set the affiliate reference
            if (application.getAffiliate() != null && application.getAffiliate().getDocumentNumber() != null) {
                AffiliateEntity affiliateEntity = affiliateRepository
                        .findByDocumentNumber(application.getAffiliate().getDocumentNumber())
                        .orElseThrow(() -> new RuntimeException("Affiliate not found"));
                entity.setAffiliate(affiliateEntity);
            }
        }

        CreditApplicationEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<CreditApplication> findByIdWithAffiliate(Long id) {
        return jpaRepository.findByIdWithAffiliate(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<CreditApplication> findByAffiliateId(Long affiliateId) {
        return mapper.toDomainList(jpaRepository.findByAffiliateId(affiliateId));
    }

    @Override
    public List<CreditApplication> findByAffiliateDocumentNumber(String documentNumber) {
        return mapper.toDomainList(jpaRepository.findByAffiliateDocumentNumber(documentNumber));
    }

    @Override
    public List<CreditApplication> findByStatus(ApplicationStatus status) {
        return mapper.toDomainList(jpaRepository.findByStatus(status));
    }

    @Override
    public List<CreditApplication> findAll() {
        return mapper.toDomainList(jpaRepository.findAllWithAffiliate());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
