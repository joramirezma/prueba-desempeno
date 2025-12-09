package com.coopcredit.application.service;

import com.coopcredit.domain.exception.AffiliateNotFoundException;
import com.coopcredit.domain.exception.DuplicateDocumentException;
import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.model.enums.AffiliateStatus;
import com.coopcredit.domain.port.output.AffiliateRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AffiliateService.
 */
@ExtendWith(MockitoExtension.class)
class AffiliateServiceTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepository;

    @InjectMocks
    private AffiliateService affiliateService;

    private Affiliate affiliate;

    @BeforeEach
    void setUp() {
        affiliate = new Affiliate();
        affiliate.setId(1L);
        affiliate.setDocumentNumber("1017654321");
        affiliate.setName("John Doe");
        affiliate.setSalary(new BigDecimal("5000000"));
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(12));
        affiliate.setStatus(AffiliateStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should register a new affiliate")
    void shouldRegisterNewAffiliate() {
        // Given
        when(affiliateRepository.existsByDocumentNumber("1017654321")).thenReturn(false);
        when(affiliateRepository.save(any(Affiliate.class)))
                .thenAnswer(invocation -> {
                    Affiliate a = invocation.getArgument(0);
                    a.setId(1L);
                    return a;
                });

        // When
        Affiliate result = affiliateService.register(affiliate);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(AffiliateStatus.ACTIVE);
        verify(affiliateRepository).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should throw exception when document already exists")
    void shouldThrowExceptionWhenDocumentExists() {
        // Given
        when(affiliateRepository.existsByDocumentNumber("1017654321")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> affiliateService.register(affiliate))
                .isInstanceOf(DuplicateDocumentException.class);
        verify(affiliateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update affiliate information")
    void shouldUpdateAffiliateInformation() {
        // Given
        when(affiliateRepository.findByDocumentNumber("1017654321"))
                .thenReturn(Optional.of(affiliate));
        when(affiliateRepository.save(any(Affiliate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Affiliate updateData = new Affiliate();
        updateData.setName("John Updated");
        updateData.setSalary(new BigDecimal("6000000"));

        // When
        Affiliate result = affiliateService.update("1017654321", updateData);

        // Then
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getSalary()).isEqualTo(new BigDecimal("6000000"));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent affiliate")
    void shouldThrowExceptionWhenUpdatingNonExistentAffiliate() {
        // Given
        when(affiliateRepository.findByDocumentNumber("9999999999"))
                .thenReturn(Optional.empty());

        Affiliate updateData = new Affiliate();
        updateData.setName("John Updated");

        // When/Then
        assertThatThrownBy(() -> affiliateService.update("9999999999", updateData))
                .isInstanceOf(AffiliateNotFoundException.class);
    }

    @Test
    @DisplayName("Should activate affiliate")
    void shouldActivateAffiliate() {
        // Given
        affiliate.setStatus(AffiliateStatus.INACTIVE);
        when(affiliateRepository.findByDocumentNumber("1017654321"))
                .thenReturn(Optional.of(affiliate));
        when(affiliateRepository.save(any(Affiliate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Affiliate result = affiliateService.activate("1017654321");

        // Then
        assertThat(result.getStatus()).isEqualTo(AffiliateStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should deactivate affiliate")
    void shouldDeactivateAffiliate() {
        // Given
        when(affiliateRepository.findByDocumentNumber("1017654321"))
                .thenReturn(Optional.of(affiliate));
        when(affiliateRepository.save(any(Affiliate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Affiliate result = affiliateService.deactivate("1017654321");

        // Then
        assertThat(result.getStatus()).isEqualTo(AffiliateStatus.INACTIVE);
    }
}
