package com.coopcredit.domain.model;

import com.coopcredit.domain.model.enums.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model representing a credit application.
 * This is a pure domain object with no framework dependencies.
 */
public class CreditApplication {

    private Long id;
    private Affiliate affiliate;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private BigDecimal proposedRate;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
    private RiskEvaluation riskEvaluation;

    public CreditApplication() {
        this.applicationDate = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }

    public CreditApplication(Long id, Affiliate affiliate, BigDecimal requestedAmount,
            Integer termMonths, BigDecimal proposedRate) {
        this.id = id;
        this.affiliate = affiliate;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.proposedRate = proposedRate;
        this.applicationDate = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }

    // Domain behavior methods

    /**
     * Calculate the estimated monthly payment.
     * Using simple interest formula: P * (1 + r*t) / t
     * Where P = principal, r = annual rate, t = term in years
     */
    public BigDecimal calculateMonthlyPayment() {
        if (requestedAmount == null || proposedRate == null || termMonths == null || termMonths == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal annualRate = proposedRate.divide(BigDecimal.valueOf(100), 6, java.math.RoundingMode.HALF_UP);
        BigDecimal termYears = BigDecimal.valueOf(termMonths).divide(BigDecimal.valueOf(12), 6,
                java.math.RoundingMode.HALF_UP);
        BigDecimal totalAmount = requestedAmount.multiply(BigDecimal.ONE.add(annualRate.multiply(termYears)));

        return totalAmount.divide(BigDecimal.valueOf(termMonths), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate the debt-to-income ratio.
     * 
     * @param monthlySalary the affiliate's monthly salary
     */
    public BigDecimal calculateDebtToIncomeRatio(BigDecimal monthlySalary) {
        if (monthlySalary == null || monthlySalary.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100); // Infinite ratio
        }
        BigDecimal monthlyPayment = calculateMonthlyPayment();
        return monthlyPayment.divide(monthlySalary, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Approve the credit application.
     */
    public void approve() {
        this.status = ApplicationStatus.APPROVED;
    }

    /**
     * Reject the credit application.
     */
    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    /**
     * Check if this application is pending.
     */
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    /**
     * Check if this application has been evaluated.
     */
    public boolean hasBeenEvaluated() {
        return this.riskEvaluation != null;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Affiliate getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(Affiliate affiliate) {
        this.affiliate = affiliate;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(BigDecimal proposedRate) {
        this.proposedRate = proposedRate;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public RiskEvaluation getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(RiskEvaluation riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
        if (riskEvaluation != null) {
            riskEvaluation.setCreditApplication(this);
        }
    }
}
