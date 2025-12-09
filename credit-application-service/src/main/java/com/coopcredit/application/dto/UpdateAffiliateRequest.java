package com.coopcredit.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for updating an affiliate.
 */
public record UpdateAffiliateRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters") String name,

        @DecimalMin(value = "0.01", message = "Salary must be greater than 0") BigDecimal salary) {
}
