package com.coopcredit.risk.service;

import com.coopcredit.risk.dto.RiskEvaluationRequest;
import com.coopcredit.risk.dto.RiskEvaluationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for calculating risk scores.
 * Uses a deterministic algorithm based on document number to ensure
 * the same document always gets the same score.
 */
@Service
public class RiskCalculatorService {

    private static final Logger log = LoggerFactory.getLogger(RiskCalculatorService.class);

    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 950;
    private static final int HIGH_RISK_THRESHOLD = 500;
    private static final int MEDIUM_RISK_THRESHOLD = 700;

    /**
     * Evaluate credit risk based on document number.
     * Implementation as per requirements:
     * - Same document always returns the same score
     * - Different documents return different scores
     * - Uses hash-based seed for deterministic results
     */
    public RiskEvaluationResponse evaluate(RiskEvaluationRequest request) {
        log.info("Evaluating risk for document: {}", request.documentNumber());

        // Generate deterministic seed from document number (hash mod 1000)
        int seed = Math.abs(request.documentNumber().hashCode() % 1000);

        // Calculate score between 300 and 950 based on seed
        int scoreRange = MAX_SCORE - MIN_SCORE;
        int score = MIN_SCORE + (seed * scoreRange / 1000);

        // Classify risk level
        String riskLevel;
        String details;

        if (score <= HIGH_RISK_THRESHOLD) {
            riskLevel = "HIGH";
            details = "High credit risk. Significant negative history detected.";
        } else if (score <= MEDIUM_RISK_THRESHOLD) {
            riskLevel = "MEDIUM";
            details = "Moderate credit risk. Some payment irregularities in history.";
        } else {
            riskLevel = "LOW";
            details = "Low credit risk. Excellent payment history and credit behavior.";
        }

        log.info("Risk evaluation complete: document={}, score={}, level={}",
                request.documentNumber(), score, riskLevel);

        return new RiskEvaluationResponse(
                request.documentNumber(),
                score,
                riskLevel,
                details);
    }
}
