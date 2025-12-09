package com.coopcredit.application.dto;

import com.coopcredit.domain.model.enums.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for credit application response.
 */
public record CreditApplicationResponse(
        Long id,
        String affiliateDocumentNumber,
        String affiliateName,
        BigDecimal requestedAmount,
        Integer termMonths,
        BigDecimal proposedRate,
        BigDecimal estimatedMonthlyPayment,
        LocalDateTime applicationDate,
        ApplicationStatus status,
        RiskEvaluationResponse riskEvaluation) {
}
