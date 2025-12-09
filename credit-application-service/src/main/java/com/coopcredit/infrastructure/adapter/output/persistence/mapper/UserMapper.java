package com.coopcredit.infrastructure.adapter.output.persistence.mapper;

import com.coopcredit.domain.model.User;
import com.coopcredit.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for User domain model and entity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);
}
