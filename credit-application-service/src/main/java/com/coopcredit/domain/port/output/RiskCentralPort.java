package com.coopcredit.domain.port.output;

import com.coopcredit.domain.model.enums.RiskLevel;
import java.math.BigDecimal;

/**
 * Output port for external risk central service.
 * This interface defines the contract for calling the external risk evaluation
 * service.
 */
public interface RiskCentralPort {

    /**
     * Request a risk evaluation from the external risk central service.
     * 
     * @param documentNumber  the affiliate's document number
     * @param requestedAmount the amount requested
     * @param termMonths      the term in months
     * @return the risk evaluation response
     */
    RiskEvaluationResponse evaluate(String documentNumber, BigDecimal requestedAmount, Integer termMonths);

    /**
     * Response from the risk central service.
     */
    record RiskEvaluationResponse(
            String documentNumber,
            Integer score,
            RiskLevel riskLevel,
            String details) {
    }
}
