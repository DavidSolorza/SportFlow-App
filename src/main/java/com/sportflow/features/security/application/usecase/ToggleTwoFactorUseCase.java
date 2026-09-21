package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ToggleTwoFactorUseCase {

    private final UserRepositoryPort userRepository;

    public ToggleTwoFactorUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean execute(UUID userId, boolean habilitar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

        if (habilitar) {
            user.habilitar2FA("SPORTFLOW-TOTP-SECRET-" + UUID.randomUUID().toString().substring(0, 8));
        } else {
            user.deshabilitar2FA();
        }

        userRepository.save(user);
        return user.isDosFactoresHabilitado();
    }
}
