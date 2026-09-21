package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.TwoFactorChallenge;

import java.util.Optional;

public interface TwoFactorRepositoryPort {
    TwoFactorChallenge save(TwoFactorChallenge challenge);
    Optional<TwoFactorChallenge> findByDesafioToken(String desafioToken);
}
