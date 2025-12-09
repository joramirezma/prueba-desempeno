package com.coopcredit.infrastructure.adapter.output.persistence.entity;

import com.coopcredit.domain.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for Credit Application.
 */
@Entity
@Table(name = "credit_applications")
@NamedEntityGraph(name = "CreditApplication.withAffiliate", attributeNodes = @NamedAttributeNode("affiliate"))
public class CreditApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliateEntity affiliate;

    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "proposed_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal proposedRate;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @OneToOne(mappedBy = "creditApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RiskEvaluationEntity riskEvaluation;

    public CreditApplicationEntity() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AffiliateEntity getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(AffiliateEntity affiliate) {
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

    public RiskEvaluationEntity getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(RiskEvaluationEntity riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
        if (riskEvaluation != null) {
            riskEvaluation.setCreditApplication(this);
        }
    }
}
