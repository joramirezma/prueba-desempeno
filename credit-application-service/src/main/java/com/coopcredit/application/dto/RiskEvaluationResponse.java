package com.coopcredit.application.dto;

import com.coopcredit.domain.model.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for risk evaluation response.
 */
public record RiskEvaluationResponse(
        Long id,
        Integer score,
        RiskLevel riskLevel,
        BigDecimal debtToIncomeRatio,
        String reason,
        String details,
        LocalDateTime evaluationDate,
        Boolean approved) {
}
