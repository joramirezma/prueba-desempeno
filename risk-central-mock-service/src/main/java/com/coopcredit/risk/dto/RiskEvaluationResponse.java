package com.coopcredit.risk.dto;

/**
 * Response DTO for risk evaluation.
 */
public record RiskEvaluationResponse(
        String documentNumber,
        Integer score,
        String riskLevel,
        String details) {
}
