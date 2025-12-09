package com.coopcredit.infrastructure.adapter.output.persistence.repository;

import com.coopcredit.domain.model.enums.ApplicationStatus;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for CreditApplicationEntity.
 */
@Repository
public interface JpaCreditApplicationRepository extends JpaRepository<CreditApplicationEntity, Long> {

    @EntityGraph(value = "CreditApplication.withAffiliate")
    Optional<CreditApplicationEntity> findWithAffiliateById(Long id);

    @Query("SELECT c FROM CreditApplicationEntity c JOIN FETCH c.affiliate WHERE c.id = :id")
    Optional<CreditApplicationEntity> findByIdWithAffiliate(@Param("id") Long id);

    List<CreditApplicationEntity> findByAffiliateId(Long affiliateId);

    @Query("SELECT c FROM CreditApplicationEntity c JOIN FETCH c.affiliate WHERE c.affiliate.documentNumber = :documentNumber")
    List<CreditApplicationEntity> findByAffiliateDocumentNumber(@Param("documentNumber") String documentNumber);

    List<CreditApplicationEntity> findByStatus(ApplicationStatus status);

    @Query("SELECT c FROM CreditApplicationEntity c JOIN FETCH c.affiliate")
    List<CreditApplicationEntity> findAllWithAffiliate();
}
