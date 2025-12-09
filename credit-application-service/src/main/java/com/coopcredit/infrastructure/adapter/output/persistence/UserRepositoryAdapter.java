package com.coopcredit.infrastructure.adapter.output.persistence;

import com.coopcredit.domain.model.User;
import com.coopcredit.domain.port.output.UserRepositoryPort;
import com.coopcredit.infrastructure.adapter.output.persistence.mapper.UserMapper;
import com.coopcredit.infrastructure.adapter.output.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA adapter implementing the UserRepositoryPort.
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(JpaUserRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
