package com.coopcredit.infrastructure.adapter.input.rest;

import com.coopcredit.application.dto.*;
import com.coopcredit.domain.exception.CreditApplicationNotFoundException;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.domain.port.input.CreditApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for credit application management endpoints.
 */
@RestController
@RequestMapping("/applications")
@Tag(name = "Credit Applications", description = "Credit application management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CreditApplicationController {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationController.class);

    private final CreditApplicationUseCase creditApplicationUseCase;

    public CreditApplicationController(CreditApplicationUseCase creditApplicationUseCase) {
        this.creditApplicationUseCase = creditApplicationUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AFFILIATE', 'ADMIN')")
    @Operation(summary = "Create credit application", description = "Creates a new credit application")
    public ResponseEntity<CreditApplicationResponse> create(
            @Valid @RequestBody CreateCreditApplicationRequest request) {
        log.info("Creating credit application for affiliate: {}", request.affiliateDocumentNumber());

        CreditApplication created = creditApplicationUseCase.create(
                request.affiliateDocumentNumber(),
                request.requestedAmount(),
                request.termMonths(),
                request.proposedRate());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AFFILIATE', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get application by ID", description = "Retrieves a credit application by ID")
    public ResponseEntity<CreditApplicationResponse> getById(@PathVariable Long id) {
        log.info("Getting credit application: {}", id);

        CreditApplication application = creditApplicationUseCase.findById(id)
                .orElseThrow(() -> new CreditApplicationNotFoundException(id));

        return ResponseEntity.ok(toResponse(application));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all applications", description = "Retrieves all credit applications (Admin only)")
    public ResponseEntity<List<CreditApplicationResponse>> getAll() {
        log.info("Getting all credit applications");

        List<CreditApplicationResponse> applications = creditApplicationUseCase.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get pending applications", description = "Retrieves all pending applications (Analyst/Admin)")
    public ResponseEntity<List<CreditApplicationResponse>> getPending() {
        log.info("Getting pending credit applications");

        List<CreditApplicationResponse> applications = creditApplicationUseCase.findPendingApplications()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/affiliate/{documentNumber}")
    @PreAuthorize("hasAnyRole('AFFILIATE', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get applications by affiliate", description = "Retrieves applications for an affiliate")
    public ResponseEntity<List<CreditApplicationResponse>> getByAffiliate(@PathVariable String documentNumber) {
        log.info("Getting credit applications for affiliate: {}", documentNumber);

        // For AFFILIATE role, verify they are accessing their own applications
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAffiliate = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AFFILIATE"));
        boolean isAdminOrAnalyst = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_ANALYST"));

        if (isAffiliate && !isAdminOrAnalyst) {
            // Get the document number from the authenticated user
            Object principal = auth.getPrincipal();
            if (principal instanceof com.coopcredit.infrastructure.security.CustomUserDetails userDetails) {
                String userDocumentNumber = userDetails.getDocumentNumber();
                if (userDocumentNumber == null || !userDocumentNumber.equals(documentNumber)) {
                    log.warn("Affiliate {} attempted to access applications for {}",
                            auth.getName(), documentNumber);
                    throw new org.springframework.security.access.AccessDeniedException(
                            "Affiliates can only view their own applications");
                }
            }
        }

        List<CreditApplicationResponse> applications = creditApplicationUseCase.findByAffiliateDocument(documentNumber)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(applications);
    }

    @PostMapping("/{id}/evaluate-risk")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Evaluate risk", description = "Performs automatic risk evaluation (Analyst/Admin)")
    public ResponseEntity<CreditApplicationResponse> evaluateRisk(@PathVariable Long id) {
        log.info("Evaluating risk for credit application: {}", id);

        CreditApplication evaluated = creditApplicationUseCase.evaluateRisk(id);
        return ResponseEntity.ok(toResponse(evaluated));
    }

    @PostMapping("/{id}/decide")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Make decision", description = "Approve or reject application manually (Analyst/Admin)")
    public ResponseEntity<CreditApplicationResponse> makeDecision(
            @PathVariable Long id,
            @RequestBody @Valid EvaluationDecisionRequest request) {
        log.info("Making decision for credit application {}: {}", id, request.approved() ? "APPROVED" : "REJECTED");

        CreditApplication decided = creditApplicationUseCase.makeDecision(id, request.approved(), request.comments());
        return ResponseEntity.ok(toResponse(decided));
    }

    private CreditApplicationResponse toResponse(CreditApplication application) {
        RiskEvaluationResponse riskResponse = null;
        if (application.getRiskEvaluation() != null) {
            RiskEvaluation re = application.getRiskEvaluation();
            riskResponse = new RiskEvaluationResponse(
                    re.getId(),
                    re.getScore(),
                    re.getRiskLevel(),
                    re.getDebtToIncomeRatio(),
                    re.getReason(),
                    re.getDetails(),
                    re.getEvaluationDate(),
                    re.getApproved());
        }

        return new CreditApplicationResponse(
                application.getId(),
                application.getAffiliate() != null ? application.getAffiliate().getDocumentNumber() : null,
                application.getAffiliate() != null ? application.getAffiliate().getName() : null,
                application.getRequestedAmount(),
                application.getTermMonths(),
                application.getProposedRate(),
                application.calculateMonthlyPayment(),
                application.getApplicationDate(),
                application.getStatus(),
                riskResponse);
    }
}
