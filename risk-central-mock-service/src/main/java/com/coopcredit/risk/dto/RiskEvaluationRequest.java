package com.coopcredit.risk.dto;

import java.math.BigDecimal;

/**
 * Request DTO for risk evaluation.
 */
public record RiskEvaluationRequest(
        String documentNumber,
        BigDecimal amount,
        Integer termMonths) {
}
