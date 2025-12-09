package com.coopcredit.application.dto;

import com.coopcredit.domain.model.enums.AffiliateStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for affiliate response.
 */
public record AffiliateResponse(
        Long id,
        String documentNumber,
        String name,
        BigDecimal salary,
        LocalDate affiliationDate,
        AffiliateStatus status,
        long monthsOfAffiliation) {
}
