package com.coopcredit.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * Custom metrics service for observability using Micrometer.
 */
@Component
public class MetricsService {

    private final Counter applicationsCreatedCounter;
    private final Counter applicationsApprovedCounter;
    private final Counter applicationsRejectedCounter;
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter affiliatesRegisteredCounter;
    private final Timer riskEvaluationTimer;

    public MetricsService(MeterRegistry registry) {
        // Credit Application Metrics
        this.applicationsCreatedCounter = Counter.builder("credit.applications.created")
                .description("Total number of credit applications created")
                .tag("type", "created")
                .register(registry);

        this.applicationsApprovedCounter = Counter.builder("credit.applications.evaluated")
                .description("Number of credit applications approved")
                .tag("result", "approved")
                .register(registry);

        this.applicationsRejectedCounter = Counter.builder("credit.applications.evaluated")
                .description("Number of credit applications rejected")
                .tag("result", "rejected")
                .register(registry);

        // Authentication Metrics
        this.loginSuccessCounter = Counter.builder("auth.login")
                .description("Number of successful logins")
                .tag("result", "success")
                .register(registry);

        this.loginFailureCounter = Counter.builder("auth.login")
                .description("Number of failed login attempts")
                .tag("result", "failure")
                .register(registry);

        // Affiliate Metrics
        this.affiliatesRegisteredCounter = Counter.builder("affiliates.registered")
                .description("Total number of affiliates registered")
                .register(registry);

        // Risk Evaluation Timer
        this.riskEvaluationTimer = Timer.builder("risk.evaluation.time")
                .description("Time taken for risk evaluation calls")
                .register(registry);
    }

    public void incrementApplicationsCreated() {
        applicationsCreatedCounter.increment();
    }

    public void incrementApplicationsApproved() {
        applicationsApprovedCounter.increment();
    }

    public void incrementApplicationsRejected() {
        applicationsRejectedCounter.increment();
    }

    public void incrementLoginSuccess() {
        loginSuccessCounter.increment();
    }

    public void incrementLoginFailure() {
        loginFailureCounter.increment();
    }

    public void incrementAffiliatesRegistered() {
        affiliatesRegisteredCounter.increment();
    }

    public Timer getRiskEvaluationTimer() {
        return riskEvaluationTimer;
    }
}
