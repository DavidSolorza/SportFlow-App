package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.PasswordResetRequest;
import com.sportflow.features.security.domain.ports.PasswordResetRepositoryPort;
import com.sportflow.features.security.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaPasswordResetRepositoryAdapter implements PasswordResetRepositoryPort {

    private final SpringDataPasswordResetRepository passwordResetRepository;

    public JpaPasswordResetRepositoryAdapter(SpringDataPasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    @Override
    public PasswordResetRequest save(PasswordResetRequest request) {
        PasswordResetJpaEntity entity = SecurityEntityMapper.toEntity(request);
        PasswordResetJpaEntity saved = passwordResetRepository.save(entity);
        return SecurityEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<PasswordResetRequest> findByTokenHash(String tokenHash) {
        return passwordResetRepository.findByTokenHash(tokenHash).map(SecurityEntityMapper::toDomain);
    }
}
