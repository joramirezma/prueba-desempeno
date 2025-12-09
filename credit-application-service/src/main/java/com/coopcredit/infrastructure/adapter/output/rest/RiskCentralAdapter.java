package com.coopcredit.infrastructure.adapter.output.rest;

import com.coopcredit.domain.model.enums.RiskLevel;
import com.coopcredit.domain.port.output.RiskCentralPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

/**
 * REST adapter for calling the external Risk Central mock service.
 */
@Component
public class RiskCentralAdapter implements RiskCentralPort {

    private static final Logger log = LoggerFactory.getLogger(RiskCentralAdapter.class);

    private final RestClient restClient;
    private final String riskServiceUrl;

    public RiskCentralAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${app.risk-service.url:http://localhost:8081}") String riskServiceUrl) {
        this.riskServiceUrl = riskServiceUrl;
        this.restClient = restClientBuilder
                .baseUrl(riskServiceUrl)
                .build();
    }

    @Override
    public RiskEvaluationResponse evaluate(String documentNumber, BigDecimal requestedAmount, Integer termMonths) {
        log.info("Calling Risk Central service for document: {}", documentNumber);

        try {
            RiskRequest request = new RiskRequest(documentNumber, requestedAmount, termMonths);

            RiskResponse response = restClient.post()
                    .uri("/risk-evaluation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(RiskResponse.class);

            if (response == null) {
                log.error("Null response from Risk Central service");
                throw new RuntimeException("Risk Central service returned null response");
            }

            log.info("Risk evaluation received: score={}, level={}", response.score(), response.riskLevel());

            return new RiskEvaluationResponse(
                    response.documentNumber(),
                    response.score(),
                    mapRiskLevel(response.riskLevel()),
                    response.details());

        } catch (Exception e) {
            log.error("Error calling Risk Central service: {}", e.getMessage());
            throw new RuntimeException("Failed to call Risk Central service: " + e.getMessage(), e);
        }
    }

    private RiskLevel mapRiskLevel(String level) {
        return switch (level.toUpperCase()) {
            case "HIGH", "ALTO" -> RiskLevel.HIGH;
            case "MEDIUM", "MEDIO" -> RiskLevel.MEDIUM;
            case "LOW", "BAJO" -> RiskLevel.LOW;
            default -> RiskLevel.HIGH; // Default to HIGH for unknown levels
        };
    }

    // Request/Response records for REST client
    private record RiskRequest(String documentNumber, BigDecimal amount, Integer termMonths) {
    }

    private record RiskResponse(String documentNumber, Integer score, String riskLevel, String details) {
    }
}
