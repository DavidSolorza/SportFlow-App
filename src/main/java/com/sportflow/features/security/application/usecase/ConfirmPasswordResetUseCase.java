package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.security.application.dto.PasswordResetDTOs;
import com.sportflow.features.security.domain.model.PasswordResetRequest;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.PasswordEncoderPort;
import com.sportflow.features.security.domain.ports.PasswordResetRepositoryPort;
import com.sportflow.features.security.domain.ports.SessionRepositoryPort;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmPasswordResetUseCase {

    private final PasswordResetRepositoryPort passwordResetRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final SessionRepositoryPort sessionRepository;

    public ConfirmPasswordResetUseCase(PasswordResetRepositoryPort passwordResetRepository,
                                       UserRepositoryPort userRepository,
                                       PasswordEncoderPort passwordEncoder,
                                       SessionRepositoryPort sessionRepository) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public PasswordResetDTOs.SimpleMessageResponse execute(PasswordResetDTOs.ConfirmResetCommand command) {
        PasswordResetRequest resetRequest = passwordResetRepository.findByTokenHash(command.token())
                .orElseThrow(() -> new BusinessRuleException("INVALID_RESET_TOKEN", "El token de recuperación es inválido o no existe."));

        if (!resetRequest.esValido()) {
            throw new BusinessRuleException("EXPIRED_OR_USED_TOKEN", "El token de recuperación ha expirado o ya ha sido utilizado previamente.");
        }

        User user = userRepository.findById(resetRequest.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

        // Actualizar contraseña con nuevo hash BCrypt
        String nuevoPasswordHash = passwordEncoder.encode(command.nuevaPassword());
        user.cambiarPassword(nuevoPasswordHash);
        userRepository.save(user);

        // Marcar token como utilizado
        resetRequest.consumir();
        passwordResetRepository.save(resetRequest);

        // HU-SE-09: Invalidar todas las sesiones previas del usuario por seguridad
        sessionRepository.revokeAllByUserId(user.getId());

        return new PasswordResetDTOs.SimpleMessageResponse(
                "Contraseña actualizada exitosamente. Todas las sesiones anteriores han sido cerradas. Inicie sesión nuevamente."
        );
    }
}
