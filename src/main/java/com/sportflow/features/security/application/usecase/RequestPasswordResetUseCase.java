package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.features.security.application.dto.PasswordResetDTOs;
import com.sportflow.features.security.domain.model.AuthProvider;
import com.sportflow.features.security.domain.model.PasswordResetRequest;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.PasswordEncoderPort;
import com.sportflow.features.security.domain.ports.PasswordResetRepositoryPort;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RequestPasswordResetUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordResetRepositoryPort passwordResetRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RequestPasswordResetUseCase(UserRepositoryPort userRepository,
                                      PasswordResetRepositoryPort passwordResetRepository,
                                      PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PasswordResetDTOs.SimpleMessageResponse execute(PasswordResetDTOs.RequestResetCommand command) {
        Optional<User> userOpt = userRepository.findByEmail(command.email().trim().toLowerCase());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // HU-SE-09: No permitir recuperación a usuarios OAuth
            if (user.getProveedorAuth() != AuthProvider.LOCAL) {
                throw new BusinessRuleException("OAUTH_PASSWORD_RESET_FORBIDDEN",
                        "Las cuentas registradas mediante proveedores OAuth (Google/GitHub) deben gestionar sus credenciales directamente con el proveedor externo.");
            }

            String plainToken = UUID.randomUUID().toString();
            String tokenHash = passwordEncoder.encode(plainToken);

            PasswordResetRequest resetRequest = PasswordResetRequest.crear(user.getId(), tokenHash, 15);
            passwordResetRepository.save(resetRequest);

            // Simulación de envío por correo electrónico (en entorno dev se imprime el token en consola)
            System.out.println("[PASSWORD RESET LINK] Token para " + user.getEmail() + ": " + plainToken);
        }

        return new PasswordResetDTOs.SimpleMessageResponse(
                "Si el correo electrónico corresponde a una cuenta registrada con credenciales tradicionales, se ha enviado un enlace de recuperación con 15 minutos de validez."
        );
    }
}
