package com.coopcredit.application.service;

import com.coopcredit.domain.exception.*;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.enums.AffiliateStatus;
import com.coopcredit.domain.model.enums.ApplicationStatus;
import com.coopcredit.domain.model.enums.RiskLevel;
import com.coopcredit.domain.port.output.AffiliateRepositoryPort;
import com.coopcredit.domain.port.output.CreditApplicationRepositoryPort;
import com.coopcredit.domain.port.output.RiskCentralPort;
import com.coopcredit.infrastructure.config.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreditApplicationService.
 */
@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

        @Mock
        private CreditApplicationRepositoryPort applicationRepository;

        @Mock
        private AffiliateRepositoryPort affiliateRepository;

        @Mock
        private RiskCentralPort riskCentralPort;

        @Mock
        private MetricsService metricsService;

        @InjectMocks
        private CreditApplicationService creditApplicationService;

        private Affiliate activeAffiliate;
        private CreditApplication pendingApplication;

        @BeforeEach
        void setUp() {
                // Setup active affiliate with 12 months of affiliation
                activeAffiliate = new Affiliate();
                activeAffiliate.setId(1L);
                activeAffiliate.setDocumentNumber("1017654321");
                activeAffiliate.setName("John Doe");
                activeAffiliate.setSalary(new BigDecimal("5000000"));
                activeAffiliate.setAffiliationDate(LocalDate.now().minusMonths(12));
                activeAffiliate.setStatus(AffiliateStatus.ACTIVE);

                // Setup pending application
                pendingApplication = new CreditApplication();
                pendingApplication.setId(1L);
                pendingApplication.setAffiliate(activeAffiliate);
                pendingApplication.setRequestedAmount(new BigDecimal("10000000"));
                pendingApplication.setTermMonths(24);
                pendingApplication.setProposedRate(new BigDecimal("12.5"));
                pendingApplication.setApplicationDate(LocalDateTime.now());
                pendingApplication.setStatus(ApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("Should create credit application for active affiliate")
        void shouldCreateCreditApplicationForActiveAffiliate() {
                // Given
                when(affiliateRepository.findByDocumentNumber("1017654321"))
                                .thenReturn(Optional.of(activeAffiliate));
                when(applicationRepository.save(any(CreditApplication.class)))
                                .thenAnswer(invocation -> {
                                        CreditApplication app = invocation.getArgument(0);
                                        app.setId(1L);
                                        return app;
                                });

                // When
                CreditApplication result = creditApplicationService.create(
                                "1017654321",
                                new BigDecimal("10000000"),
                                24,
                                new BigDecimal("12.5"));

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(ApplicationStatus.PENDING);
                assertThat(result.getAffiliate().getDocumentNumber()).isEqualTo("1017654321");
                verify(applicationRepository).save(any(CreditApplication.class));
        }

        @Test
        @DisplayName("Should throw exception when affiliate not found")
        void shouldThrowExceptionWhenAffiliateNotFound() {
                // Given
                when(affiliateRepository.findByDocumentNumber("9999999999"))
                                .thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> creditApplicationService.create(
                                "9999999999",
                                new BigDecimal("10000000"),
                                24,
                                new BigDecimal("12.5"))).isInstanceOf(AffiliateNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when affiliate is inactive")
        void shouldThrowExceptionWhenAffiliateIsInactive() {
                // Given
                activeAffiliate.setStatus(AffiliateStatus.INACTIVE);
                when(affiliateRepository.findByDocumentNumber("1017654321"))
                                .thenReturn(Optional.of(activeAffiliate));

                // When/Then
                assertThatThrownBy(() -> creditApplicationService.create(
                                "1017654321",
                                new BigDecimal("10000000"),
                                24,
                                new BigDecimal("12.5"))).isInstanceOf(InactiveAffiliateException.class);
        }

        @Test
        @DisplayName("Should approve application when all criteria met")
        void shouldApproveApplicationWhenAllCriteriaMet() {
                // Given
                when(applicationRepository.findByIdWithAffiliate(1L))
                                .thenReturn(Optional.of(pendingApplication));

                RiskCentralPort.RiskEvaluationResponse riskResponse = new RiskCentralPort.RiskEvaluationResponse(
                                "1017654321", 750, RiskLevel.LOW, "Good credit history");
                when(riskCentralPort.evaluate(any(), any(), any())).thenReturn(riskResponse);
                when(applicationRepository.save(any(CreditApplication.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                CreditApplication result = creditApplicationService.evaluate(1L);

                // Then
                assertThat(result.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
                assertThat(result.getRiskEvaluation()).isNotNull();
                assertThat(result.getRiskEvaluation().getApproved()).isTrue();
        }

        @Test
        @DisplayName("Should reject application when risk level is HIGH")
        void shouldRejectApplicationWhenRiskLevelIsHigh() {
                // Given
                when(applicationRepository.findByIdWithAffiliate(1L))
                                .thenReturn(Optional.of(pendingApplication));

                RiskCentralPort.RiskEvaluationResponse riskResponse = new RiskCentralPort.RiskEvaluationResponse(
                                "1017654321", 400, RiskLevel.HIGH, "Bad credit history");
                when(riskCentralPort.evaluate(any(), any(), any())).thenReturn(riskResponse);
                when(applicationRepository.save(any(CreditApplication.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                CreditApplication result = creditApplicationService.evaluate(1L);

                // Then
                assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
                assertThat(result.getRiskEvaluation().getApproved()).isFalse();
        }

        @Test
        @DisplayName("Should reject application when amount exceeds maximum")
        void shouldRejectApplicationWhenAmountExceedsMaximum() {
                // Given
                pendingApplication.setRequestedAmount(new BigDecimal("100000000")); // Way over 12x salary
                when(applicationRepository.findByIdWithAffiliate(1L))
                                .thenReturn(Optional.of(pendingApplication));

                RiskCentralPort.RiskEvaluationResponse riskResponse = new RiskCentralPort.RiskEvaluationResponse(
                                "1017654321", 800, RiskLevel.LOW, "Good credit");
                when(riskCentralPort.evaluate(any(), any(), any())).thenReturn(riskResponse);
                when(applicationRepository.save(any(CreditApplication.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                CreditApplication result = creditApplicationService.evaluate(1L);

                // Then
                assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
                assertThat(result.getRiskEvaluation().getReason()).contains("maximum allowed");
        }

        @Test
        @DisplayName("Should reject application when insufficient affiliation time")
        void shouldRejectApplicationWhenInsufficientAffiliationTime() {
                // Given - affiliate with only 3 months
                activeAffiliate.setAffiliationDate(LocalDate.now().minusMonths(3));
                when(applicationRepository.findByIdWithAffiliate(1L))
                                .thenReturn(Optional.of(pendingApplication));

                RiskCentralPort.RiskEvaluationResponse riskResponse = new RiskCentralPort.RiskEvaluationResponse(
                                "1017654321", 800, RiskLevel.LOW, "Good credit");
                when(riskCentralPort.evaluate(any(), any(), any())).thenReturn(riskResponse);
                when(applicationRepository.save(any(CreditApplication.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                CreditApplication result = creditApplicationService.evaluate(1L);

                // Then
                assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
                assertThat(result.getRiskEvaluation().getReason()).contains("affiliation time");
        }
}
