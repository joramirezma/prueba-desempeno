package com.coopcredit.domain.model;

import com.coopcredit.domain.model.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model representing the risk evaluation of a credit application.
 * This is a pure domain object with no framework dependencies.
 */
public class RiskEvaluation {

    private Long id;
    private CreditApplication creditApplication;
    private Integer score;
    private RiskLevel riskLevel;
    private BigDecimal debtToIncomeRatio;
    private String reason;
    private String details;
    private LocalDateTime evaluationDate;
    private Boolean approved;

    public RiskEvaluation() {
        this.evaluationDate = LocalDateTime.now();
    }

    public RiskEvaluation(Integer score, RiskLevel riskLevel, BigDecimal debtToIncomeRatio,
            String reason, Boolean approved) {
        this.score = score;
        this.riskLevel = riskLevel;
        this.debtToIncomeRatio = debtToIncomeRatio;
        this.reason = reason;
        this.approved = approved;
        this.evaluationDate = LocalDateTime.now();
    }

    // Domain behavior methods

    /**
     * Check if the risk level is acceptable for credit approval.
     * HIGH risk is not acceptable.
     */
    public boolean isRiskAcceptable() {
        return riskLevel != RiskLevel.HIGH;
    }

    /**
     * Check if debt-to-income ratio is within acceptable limits.
     * Business rule: Maximum 40% of income for debt.
     */
    public boolean isDebtToIncomeAcceptable() {
        if (debtToIncomeRatio == null) {
            return false;
        }
        return debtToIncomeRatio.compareTo(BigDecimal.valueOf(40)) <= 0;
    }

    /**
     * Create a summary of the evaluation.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Risk Score: ").append(score);
        sb.append(", Level: ").append(riskLevel);
        sb.append(", DTI: ").append(debtToIncomeRatio).append("%");
        sb.append(", Decision: ").append(approved ? "APPROVED" : "REJECTED");
        if (reason != null) {
            sb.append(", Reason: ").append(reason);
        }
        return sb.toString();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplication getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplication creditApplication) {
        this.creditApplication = creditApplication;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
