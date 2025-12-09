package com.coopcredit.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new affiliate.
 */
public record CreateAffiliateRequest(
        @NotBlank(message = "Document number is required") @Size(min = 5, max = 20, message = "Document number must be between 5 and 20 characters") String documentNumber,

        @NotBlank(message = "Name is required") @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters") String name,

        @NotNull(message = "Salary is required") @DecimalMin(value = "0.01", message = "Salary must be greater than 0") BigDecimal salary,

        @NotNull(message = "Affiliation date is required") @PastOrPresent(message = "Affiliation date cannot be in the future") LocalDate affiliationDate) {
}
