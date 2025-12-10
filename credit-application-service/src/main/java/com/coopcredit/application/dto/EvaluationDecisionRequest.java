package com.coopcredit.application.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for analyst decision on credit application.
 */
public record EvaluationDecisionRequest(
        @NotNull(message = "Decision (approved/rejected) is required")
        Boolean approved,
        
        String comments
) {
}
