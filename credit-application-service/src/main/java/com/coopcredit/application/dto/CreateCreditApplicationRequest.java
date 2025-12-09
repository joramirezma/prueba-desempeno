package com.coopcredit.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for creating a credit application.
 */
public record CreateCreditApplicationRequest(
        @NotBlank(message = "Affiliate document number is required") String affiliateDocumentNumber,

        @NotNull(message = "Requested amount is required") @DecimalMin(value = "100000", message = "Minimum credit amount is 100,000") @DecimalMax(value = "500000000", message = "Maximum credit amount is 500,000,000") BigDecimal requestedAmount,

        @NotNull(message = "Term in months is required") @Min(value = 6, message = "Minimum term is 6 months") @Max(value = 120, message = "Maximum term is 120 months") Integer termMonths,

        @NotNull(message = "Proposed rate is required") @DecimalMin(value = "0.1", message = "Minimum rate is 0.1%") @DecimalMax(value = "50", message = "Maximum rate is 50%") BigDecimal proposedRate) {
}
