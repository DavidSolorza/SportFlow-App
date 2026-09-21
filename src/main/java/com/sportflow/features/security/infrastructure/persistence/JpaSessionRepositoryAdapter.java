package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.UserSession;
import com.sportflow.features.security.domain.ports.SessionRepositoryPort;
import com.sportflow.features.security.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSessionRepositoryAdapter implements SessionRepositoryPort {

    private final SpringDataSesionRepository sesionRepository;

    public JpaSessionRepositoryAdapter(SpringDataSesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    @Override
    @Transactional
    public UserSession save(UserSession session) {
        SesionJpaEntity entity = SecurityEntityMapper.toEntity(session);
        SesionJpaEntity saved = sesionRepository.save(entity);
        return SecurityEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<UserSession> findByTokenJti(String tokenJti) {
        return sesionRepository.findByTokenJti(tokenJti).map(SecurityEntityMapper::toDomain);
    }

    @Override
    @Transactional
    public void revokeByTokenJti(String tokenJti) {
        sesionRepository.revokeByTokenJti(tokenJti);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        sesionRepository.revokeAllByUserId(userId);
    }
}
