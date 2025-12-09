package com.coopcredit.infrastructure.adapter.input.rest;

import com.coopcredit.application.dto.*;
import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.enums.AffiliateStatus;
import com.coopcredit.domain.port.input.AffiliateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for affiliate management endpoints.
 */
@RestController
@RequestMapping("/api/affiliates")
@Tag(name = "Affiliates", description = "Affiliate management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AffiliateController {

    private static final Logger log = LoggerFactory.getLogger(AffiliateController.class);

    private final AffiliateUseCase affiliateUseCase;

    public AffiliateController(AffiliateUseCase affiliateUseCase) {
        this.affiliateUseCase = affiliateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new affiliate", description = "Creates a new affiliate (Admin only)")
    public ResponseEntity<AffiliateResponse> create(@Valid @RequestBody CreateAffiliateRequest request) {
        log.info("Creating affiliate with document: {}", request.documentNumber());

        Affiliate affiliate = new Affiliate();
        affiliate.setDocumentNumber(request.documentNumber());
        affiliate.setName(request.name());
        affiliate.setSalary(request.salary());
        affiliate.setAffiliationDate(request.affiliationDate());
        affiliate.setStatus(AffiliateStatus.ACTIVE);

        Affiliate created = affiliateUseCase.register(affiliate);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{documentNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'AFFILIATE')")
    @Operation(summary = "Get affiliate by document", description = "Retrieves affiliate information")
    public ResponseEntity<AffiliateResponse> getByDocument(@PathVariable String documentNumber) {
        log.info("Getting affiliate by document: {}", documentNumber);

        Affiliate affiliate = affiliateUseCase.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(documentNumber));

        return ResponseEntity.ok(toResponse(affiliate));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get all affiliates", description = "Retrieves all affiliates (Admin/Analyst only)")
    public ResponseEntity<List<AffiliateResponse>> getAll() {
        log.info("Getting all affiliates");

        List<AffiliateResponse> affiliates = affiliateUseCase.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(affiliates);
    }

    @PutMapping("/{documentNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update affiliate", description = "Updates affiliate information (Admin only)")
    public ResponseEntity<AffiliateResponse> update(
            @PathVariable String documentNumber,
            @Valid @RequestBody UpdateAffiliateRequest request) {
        log.info("Updating affiliate: {}", documentNumber);

        Affiliate updateData = new Affiliate();
        updateData.setName(request.name());
        updateData.setSalary(request.salary());

        Affiliate updated = affiliateUseCase.update(documentNumber, updateData);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PostMapping("/{documentNumber}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate affiliate", description = "Activates an affiliate (Admin only)")
    public ResponseEntity<AffiliateResponse> activate(@PathVariable String documentNumber) {
        log.info("Activating affiliate: {}", documentNumber);

        Affiliate activated = affiliateUseCase.activate(documentNumber);
        return ResponseEntity.ok(toResponse(activated));
    }

    @PostMapping("/{documentNumber}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate affiliate", description = "Deactivates an affiliate (Admin only)")
    public ResponseEntity<AffiliateResponse> deactivate(@PathVariable String documentNumber) {
        log.info("Deactivating affiliate: {}", documentNumber);

        Affiliate deactivated = affiliateUseCase.deactivate(documentNumber);
        return ResponseEntity.ok(toResponse(deactivated));
    }

    private AffiliateResponse toResponse(Affiliate affiliate) {
        return new AffiliateResponse(
                affiliate.getId(),
                affiliate.getDocumentNumber(),
                affiliate.getName(),
                affiliate.getSalary(),
                affiliate.getAffiliationDate(),
                affiliate.getStatus(),
                affiliate.getMonthsOfAffiliation());
    }
}
