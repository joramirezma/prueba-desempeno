package com.coopcredit.infrastructure.adapter.output.persistence.mapper;

import com.coopcredit.domain.model.CreditApplication;
import com.coopcredit.domain.model.RiskEvaluation;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.CreditApplicationEntity;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.RiskEvaluationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for CreditApplication domain model and entity.
 */
@Mapper(componentModel = "spring", uses = { AffiliateMapper.class })
public interface CreditApplicationMapper {

    @Mapping(source = "affiliate", target = "affiliate")
    @Mapping(source = "riskEvaluation", target = "riskEvaluation")
    CreditApplication toDomain(CreditApplicationEntity entity);

    @Mapping(source = "affiliate", target = "affiliate")
    @Mapping(target = "riskEvaluation", ignore = true)
    CreditApplicationEntity toEntity(CreditApplication domain);

    List<CreditApplication> toDomainList(List<CreditApplicationEntity> entities);

    @Mapping(target = "creditApplication", ignore = true)
    RiskEvaluation riskEvaluationToDomain(RiskEvaluationEntity entity);

    @Mapping(target = "creditApplication", ignore = true)
    RiskEvaluationEntity riskEvaluationToEntity(RiskEvaluation domain);
}
