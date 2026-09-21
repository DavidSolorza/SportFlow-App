package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.TwoFactorChallenge;
import com.sportflow.features.security.domain.ports.TwoFactorRepositoryPort;
import com.sportflow.features.security.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaTwoFactorRepositoryAdapter implements TwoFactorRepositoryPort {

    private final SpringDataTwoFactorRepository twoFactorRepository;

    public JpaTwoFactorRepositoryAdapter(SpringDataTwoFactorRepository twoFactorRepository) {
        this.twoFactorRepository = twoFactorRepository;
    }

    @Override
    public TwoFactorChallenge save(TwoFactorChallenge challenge) {
        TwoFactorJpaEntity entity = SecurityEntityMapper.toEntity(challenge);
        TwoFactorJpaEntity saved = twoFactorRepository.save(entity);
        return SecurityEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<TwoFactorChallenge> findByDesafioToken(String desafioToken) {
        return twoFactorRepository.findByDesafioToken(desafioToken).map(SecurityEntityMapper::toDomain);
    }
}
