package com.sportflow.features.security.application.usecase;

import com.sportflow.features.security.application.dto.PasswordResetDTOs;
import com.sportflow.features.security.domain.ports.SessionRepositoryPort;
import com.sportflow.features.security.domain.ports.TokenGeneratorPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutUseCase {

    private final SessionRepositoryPort sessionRepository;
    private final TokenGeneratorPort tokenGenerator;

    public LogoutUseCase(SessionRepositoryPort sessionRepository, TokenGeneratorPort tokenGenerator) {
        this.sessionRepository = sessionRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @Transactional
    public PasswordResetDTOs.SimpleMessageResponse execute(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String jti = tokenGenerator.extractJti(token);
            if (jti != null) {
                sessionRepository.revokeByTokenJti(jti);
            }
        }
        return new PasswordResetDTOs.SimpleMessageResponse("Sesión cerrada correctamente. El token ha sido revocado.");
    }
}
