package com.coopcredit.infrastructure.adapter.output.persistence;

import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.domain.port.output.AffiliateRepositoryPort;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import com.coopcredit.infrastructure.adapter.output.persistence.mapper.AffiliateMapper;
import com.coopcredit.infrastructure.adapter.output.persistence.repository.JpaAffiliateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the AffiliateRepositoryPort.
 */
@Component
public class AffiliateRepositoryAdapter implements AffiliateRepositoryPort {

    private final JpaAffiliateRepository jpaRepository;
    private final AffiliateMapper mapper;

    public AffiliateRepositoryAdapter(JpaAffiliateRepository jpaRepository, AffiliateMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = mapper.toEntity(affiliate);
        AffiliateEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocumentNumber(String documentNumber) {
        return jpaRepository.findByDocumentNumber(documentNumber)
                .map(mapper::toDomain);
    }

    @Override
    public List<Affiliate> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
