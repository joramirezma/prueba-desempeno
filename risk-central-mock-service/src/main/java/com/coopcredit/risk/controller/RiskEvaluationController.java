package com.coopcredit.risk.controller;

import com.coopcredit.risk.dto.RiskEvaluationRequest;
import com.coopcredit.risk.dto.RiskEvaluationResponse;
import com.coopcredit.risk.service.RiskCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for risk evaluation endpoint.
 */
@RestController
@RequestMapping
public class RiskEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(RiskEvaluationController.class);

    private final RiskCalculatorService riskCalculatorService;

    public RiskEvaluationController(RiskCalculatorService riskCalculatorService) {
        this.riskCalculatorService = riskCalculatorService;
    }

    @PostMapping("/risk-evaluation")
    public ResponseEntity<RiskEvaluationResponse> evaluate(@RequestBody RiskEvaluationRequest request) {
        log.info("Received risk evaluation request for document: {}", request.documentNumber());

        RiskEvaluationResponse response = riskCalculatorService.evaluate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Risk Central Mock Service is running");
    }
}
