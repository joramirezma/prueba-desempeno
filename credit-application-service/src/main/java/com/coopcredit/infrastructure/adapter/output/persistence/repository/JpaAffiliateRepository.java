package com.coopcredit.infrastructure.adapter.output.persistence.repository;

import com.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for AffiliateEntity.
 */
@Repository
public interface JpaAffiliateRepository extends JpaRepository<AffiliateEntity, Long> {

    Optional<AffiliateEntity> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);
}
