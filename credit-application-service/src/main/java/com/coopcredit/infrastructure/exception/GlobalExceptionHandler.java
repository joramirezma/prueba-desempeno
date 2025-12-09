package com.coopcredit.infrastructure.exception;

import com.coopcredit.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler implementing RFC 7807 Problem Details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AffiliateNotFoundException.class)
    public ProblemDetail handleAffiliateNotFound(AffiliateNotFoundException ex, WebRequest request) {
        log.warn("Affiliate not found: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                "Affiliate Not Found",
                ex.getMessage(),
                request,
                "AFFILIATE_NOT_FOUND");
    }

    @ExceptionHandler(CreditApplicationNotFoundException.class)
    public ProblemDetail handleApplicationNotFound(CreditApplicationNotFoundException ex, WebRequest request) {
        log.warn("Credit application not found: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                "Credit Application Not Found",
                ex.getMessage(),
                request,
                "APPLICATION_NOT_FOUND");
    }

    @ExceptionHandler(DuplicateDocumentException.class)
    public ProblemDetail handleDuplicateDocument(DuplicateDocumentException ex, WebRequest request) {
        log.warn("Duplicate document: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "Duplicate Document",
                ex.getMessage(),
                request,
                "DUPLICATE_DOCUMENT");
    }

    @ExceptionHandler(InactiveAffiliateException.class)
    public ProblemDetail handleInactiveAffiliate(InactiveAffiliateException ex, WebRequest request) {
        log.warn("Inactive affiliate: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Inactive Affiliate",
                ex.getMessage(),
                request,
                "INACTIVE_AFFILIATE");
    }

    @ExceptionHandler(CreditEvaluationException.class)
    public ProblemDetail handleCreditEvaluation(CreditEvaluationException ex, WebRequest request) {
        log.warn("Credit evaluation error: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Credit Evaluation Error",
                ex.getMessage(),
                request,
                "EVALUATION_ERROR");
    }

    @ExceptionHandler(InsufficientAffiliationTimeException.class)
    public ProblemDetail handleInsufficientAffiliation(InsufficientAffiliationTimeException ex, WebRequest request) {
        log.warn("Insufficient affiliation time: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Insufficient Affiliation Time",
                ex.getMessage(),
                request,
                "INSUFFICIENT_AFFILIATION");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                "User Not Found",
                ex.getMessage(),
                request,
                "USER_NOT_FOUND");
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex, WebRequest request) {
        log.warn("Domain exception: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Domain Error",
                ex.getMessage(),
                request,
                ex.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "One or more fields failed validation",
                request,
                "VALIDATION_ERROR");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You do not have permission to access this resource",
                request,
                "ACCESS_DENIED");
    }

    @ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
    public ProblemDetail handleAuthentication(Exception ex, WebRequest request) {
        log.warn("Authentication error: {}", ex.getMessage());
        return createProblemDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid credentials or authentication required",
                request,
                "AUTHENTICATION_FAILED");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request,
                "INTERNAL_ERROR");
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail,
            WebRequest request, String errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/" + errorCode.toLowerCase()));
        problemDetail.setProperty("timestamp", Instant.now().toString());

        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        problemDetail.setProperty("traceId", traceId);
        problemDetail.setProperty("errorCode", errorCode);

        return problemDetail;
    }
}
