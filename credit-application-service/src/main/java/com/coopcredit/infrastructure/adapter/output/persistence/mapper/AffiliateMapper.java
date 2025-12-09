package com.coopcredit.infrastructure.adapter.output.persistence.mapper;

import com.coopcredit.domain.model.Affiliate;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Affiliate domain model and entity.
 */
@Mapper(componentModel = "spring")
public interface AffiliateMapper {

    @Mapping(target = "creditApplications", ignore = true)
    Affiliate toDomain(AffiliateEntity entity);

    @Mapping(target = "creditApplications", ignore = true)
    AffiliateEntity toEntity(Affiliate domain);

    List<Affiliate> toDomainList(List<AffiliateEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditApplications", ignore = true)
    void updateEntity(@MappingTarget AffiliateEntity entity, Affiliate domain);
}
