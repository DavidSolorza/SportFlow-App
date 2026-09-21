package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.UserSession;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {
    UserSession save(UserSession session);
    Optional<UserSession> findByTokenJti(String tokenJti);
    void revokeByTokenJti(String tokenJti);
    void revokeAllByUserId(UUID userId);
}
