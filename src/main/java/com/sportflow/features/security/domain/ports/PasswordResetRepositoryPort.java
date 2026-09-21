package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.PasswordResetRequest;

import java.util.Optional;

public interface PasswordResetRepositoryPort {
    PasswordResetRequest save(PasswordResetRequest request);
    Optional<PasswordResetRequest> findByTokenHash(String tokenHash);
}
