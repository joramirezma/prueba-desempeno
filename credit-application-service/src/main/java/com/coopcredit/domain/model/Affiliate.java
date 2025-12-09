package com.coopcredit.domain.model;

import com.coopcredit.domain.model.enums.AffiliateStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing an affiliate (member) of the credit cooperative.
 * This is a pure domain object with no framework dependencies.
 */
public class Affiliate {

    private Long id;
    private String documentNumber;
    private String name;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private AffiliateStatus status;
    private List<CreditApplication> creditApplications;

    public Affiliate() {
        this.creditApplications = new ArrayList<>();
    }

    public Affiliate(Long id, String documentNumber, String name, BigDecimal salary,
            LocalDate affiliationDate, AffiliateStatus status) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.name = name;
        this.salary = salary;
        this.affiliationDate = affiliationDate;
        this.status = status;
        this.creditApplications = new ArrayList<>();
    }

    // Domain behavior methods

    /**
     * Check if the affiliate is active and can apply for credit.
     */
    public boolean canApplyForCredit() {
        return this.status == AffiliateStatus.ACTIVE;
    }

    /**
     * Calculate the months since affiliation.
     */
    public long getMonthsOfAffiliation() {
        if (affiliationDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MONTHS.between(affiliationDate, LocalDate.now());
    }

    /**
     * Check if affiliate has minimum required affiliation time.
     * 
     * @param minimumMonths minimum months required
     */
    public boolean hasMinimumAffiliationTime(int minimumMonths) {
        return getMonthsOfAffiliation() >= minimumMonths;
    }

    /**
     * Calculate maximum allowed credit amount based on salary.
     * Business rule: Maximum credit is 12 times the monthly salary.
     */
    public BigDecimal getMaximumCreditAmount() {
        if (salary == null) {
            return BigDecimal.ZERO;
        }
        return salary.multiply(BigDecimal.valueOf(12));
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getAffiliationDate() {
        return affiliationDate;
    }

    public void setAffiliationDate(LocalDate affiliationDate) {
        this.affiliationDate = affiliationDate;
    }

    public AffiliateStatus getStatus() {
        return status;
    }

    public void setStatus(AffiliateStatus status) {
        this.status = status;
    }

    public List<CreditApplication> getCreditApplications() {
        return creditApplications;
    }

    public void setCreditApplications(List<CreditApplication> creditApplications) {
        this.creditApplications = creditApplications;
    }

    public void addCreditApplication(CreditApplication application) {
        this.creditApplications.add(application);
        application.setAffiliate(this);
    }
}
