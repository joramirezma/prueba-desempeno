package com.coopcredit.application.service;

import com.coopcredit.domain.exception.*;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.domain.model.enums.ApplicationStatus;
import com.coopcredit.domain.model.enums.RiskLevel;
import com.coopcredit.domain.port.input.CreditApplicationUseCase;
import com.coopcredit.domain.port.output.AffiliateRepositoryPort;
import com.coopcredit.domain.port.output.CreditApplicationRepositoryPort;
import com.coopcredit.domain.port.output.RiskCentralPort;
import com.coopcredit.infrastructure.config.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Application service implementing credit application use cases.
 * Contains the core business logic for credit evaluation.
 */
@Service
@Transactional
public class CreditApplicationService implements CreditApplicationUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationService.class);

    // Business rule constants
    private static final int MINIMUM_AFFILIATION_MONTHS = 6;
    private static final BigDecimal MAX_DEBT_TO_INCOME_RATIO = new BigDecimal("40"); // 40%
    private static final int SALARY_MULTIPLIER_FOR_MAX_CREDIT = 12;

    private final CreditApplicationRepositoryPort applicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;
    private final RiskCentralPort riskCentralPort;
    private final MetricsService metricsService;

    public CreditApplicationService(
            CreditApplicationRepositoryPort applicationRepository,
            AffiliateRepositoryPort affiliateRepository,
            RiskCentralPort riskCentralPort,
            MetricsService metricsService) {
        this.applicationRepository = applicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.riskCentralPort = riskCentralPort;
        this.metricsService = metricsService;
    }

    @Override
    public CreditApplication create(String affiliateDocumentNumber, BigDecimal requestedAmount,
            Integer termMonths, BigDecimal proposedRate) {
        log.info("Creating credit application for affiliate: {}, amount: {}",
                affiliateDocumentNumber, requestedAmount);

        // Find and validate affiliate
        Affiliate affiliate = affiliateRepository.findByDocumentNumber(affiliateDocumentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(affiliateDocumentNumber));

        // Validate affiliate is active
        if (!affiliate.canApplyForCredit()) {
            throw new InactiveAffiliateException(affiliateDocumentNumber);
        }

        // Create the application
        CreditApplication application = new CreditApplication();
        application.setAffiliate(affiliate);
        application.setRequestedAmount(requestedAmount);
        application.setTermMonths(termMonths);
        application.setProposedRate(proposedRate);
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);

        CreditApplication saved = applicationRepository.save(application);
        metricsService.incrementApplicationsCreated();
        log.info("Credit application created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public CreditApplication evaluateRisk(Long applicationId) {
        log.info("Evaluating risk for credit application: {}", applicationId);

        // Find application with affiliate data
        CreditApplication application = applicationRepository.findByIdWithAffiliate(applicationId)
                .orElseThrow(() -> new CreditApplicationNotFoundException(applicationId));

        // Validate application is pending
        if (!application.isPending()) {
            throw new CreditEvaluationException("Application has already been evaluated");
        }

        Affiliate affiliate = application.getAffiliate();
        List<String> warnings = new ArrayList<>();

        // Calculate metrics (but don't auto-reject)
        
        // Check 1: Minimum affiliation time
        if (!affiliate.hasMinimumAffiliationTime(MINIMUM_AFFILIATION_MONTHS)) {
            warnings.add("Insufficient affiliation time. Required: " + MINIMUM_AFFILIATION_MONTHS + " months");
        }

        // Check 2: Maximum credit amount based on salary
        BigDecimal maxCreditAmount = affiliate.getMaximumCreditAmount();
        if (application.getRequestedAmount().compareTo(maxCreditAmount) > 0) {
            warnings.add("Requested amount exceeds maximum allowed: " + maxCreditAmount);
        }

        // Check 3: Debt-to-income ratio
        BigDecimal debtToIncomeRatio = application.calculateDebtToIncomeRatio(affiliate.getSalary());
        if (debtToIncomeRatio.compareTo(MAX_DEBT_TO_INCOME_RATIO) > 0) {
            warnings.add("Debt-to-income ratio too high: " + debtToIncomeRatio + "% (max: "
                    + MAX_DEBT_TO_INCOME_RATIO + "%)");
        }

        // Check 4: Call external risk central service
        RiskCentralPort.RiskEvaluationResponse riskResponse = riskCentralPort.evaluate(
                affiliate.getDocumentNumber(),
                application.getRequestedAmount(),
                application.getTermMonths());

        // Check 5: Add warning for high risk
        if (riskResponse.riskLevel() == RiskLevel.HIGH) {
            warnings.add("High risk level from credit bureau: score " + riskResponse.score());
        }

        // Create risk evaluation (as information, not decision)
        RiskEvaluation riskEvaluation = new RiskEvaluation();
        riskEvaluation.setScore(riskResponse.score());
        riskEvaluation.setRiskLevel(riskResponse.riskLevel());
        riskEvaluation.setDebtToIncomeRatio(debtToIncomeRatio);
        riskEvaluation.setDetails(riskResponse.details());
        riskEvaluation.setEvaluationDate(LocalDateTime.now());
        
        // Set warnings as reason (for analyst to review)
        if (!warnings.isEmpty()) {
            riskEvaluation.setReason(String.join("; ", warnings));
        } else {
            riskEvaluation.setReason("All evaluation criteria met successfully");
        }
        
        // Don't set approved/rejected yet - that's for the analyst
        riskEvaluation.setApproved(null);

        // Associate evaluation with application
        application.setRiskEvaluation(riskEvaluation);

        // Save and return (still PENDING)
        CreditApplication saved = applicationRepository.save(application);
        log.info("Risk evaluation completed for application: {}", applicationId);
        return saved;
    }

    @Override
    public CreditApplication makeDecision(Long applicationId, boolean approved, String comments) {
        log.info("Making decision for credit application {}: {}", applicationId, approved ? "APPROVED" : "REJECTED");

        // Find application with affiliate data
        CreditApplication application = applicationRepository.findByIdWithAffiliate(applicationId)
                .orElseThrow(() -> new CreditApplicationNotFoundException(applicationId));

        // Validate application is pending
        if (!application.isPending()) {
            throw new CreditEvaluationException("Application has already been evaluated");
        }

        // Validate risk evaluation exists
        if (application.getRiskEvaluation() == null) {
            throw new CreditEvaluationException("Risk evaluation must be performed before making a decision");
        }

        // Update risk evaluation with analyst decision
        RiskEvaluation riskEvaluation = application.getRiskEvaluation();
        riskEvaluation.setApproved(approved);
        
        // Add analyst comments to existing reason
        if (comments != null && !comments.trim().isEmpty()) {
            String existingReason = riskEvaluation.getReason();
            riskEvaluation.setReason(existingReason + " | Analyst comments: " + comments);
        }

        // Update application status
        if (approved) {
            application.approve();
            metricsService.incrementApplicationsApproved();
            log.info("Credit application {} APPROVED by analyst", applicationId);
        } else {
            application.reject();
            metricsService.incrementApplicationsRejected();
            log.info("Credit application {} REJECTED by analyst", applicationId);
        }

        // Save and return
        return applicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CreditApplication> findById(Long id) {
        log.debug("Finding credit application by ID: {}", id);
        return applicationRepository.findByIdWithAffiliate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> findByAffiliateDocument(String affiliateDocumentNumber) {
        log.debug("Finding credit applications for affiliate: {}", affiliateDocumentNumber);
        return applicationRepository.findByAffiliateDocumentNumber(affiliateDocumentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> findByStatus(ApplicationStatus status) {
        log.debug("Finding credit applications by status: {}", status);
        return applicationRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> findPendingApplications() {
        log.debug("Finding pending credit applications");
        return applicationRepository.findByStatus(ApplicationStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplication> findAll() {
        log.debug("Finding all credit applications");
        return applicationRepository.findAll();
    }
}
