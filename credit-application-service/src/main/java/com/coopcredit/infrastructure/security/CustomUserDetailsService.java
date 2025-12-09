package com.coopcredit.infrastructure.security;

import com.coopcredit.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.coopcredit.infrastructure.adapter.output.persistence.repository.JpaUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * Returns CustomUserDetails with documentNumber for affiliate access control.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUserRepository userRepository;

    public CustomUserDetailsService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        var authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new CustomUserDetails(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities,
                userEntity.getDocumentNumber());
    }
}
