package com.coopcredit.infrastructure.adapter.output.persistence.entity;

import com.coopcredit.domain.model.enums.RiskLevel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for Risk Evaluation.
 */
@Entity
@Table(name = "risk_evaluations")
public class RiskEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false, unique = true)
    private CreditApplicationEntity creditApplication;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(name = "debt_to_income_ratio", precision = 5, scale = 2)
    private BigDecimal debtToIncomeRatio;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String details;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDateTime evaluationDate;

    @Column(nullable = false)
    private Boolean approved;

    public RiskEvaluationEntity() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplicationEntity getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplicationEntity creditApplication) {
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
