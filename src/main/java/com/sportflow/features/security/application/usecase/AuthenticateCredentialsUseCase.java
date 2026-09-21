package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.UnauthorizedException;
import com.sportflow.features.security.application.dto.AuthResponse;
import com.sportflow.features.security.application.dto.LoginCommand;
import com.sportflow.features.security.domain.model.TwoFactorChallenge;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.model.UserSession;
import com.sportflow.features.security.domain.model.UserStatus;
import com.sportflow.features.security.domain.ports.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticateCredentialsUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final SessionRepositoryPort sessionRepository;
    private final TwoFactorRepositoryPort twoFactorRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthenticateCredentialsUseCase(UserRepositoryPort userRepository,
                                          PasswordEncoderPort passwordEncoder,
                                          TokenGeneratorPort tokenGenerator,
                                          SessionRepositoryPort sessionRepository,
                                          TwoFactorRepositoryPort twoFactorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.sessionRepository = sessionRepository;
        this.twoFactorRepository = twoFactorRepository;
    }

    @Transactional
    public AuthResponse execute(LoginCommand command, String ipOrigen, String userAgent) {
        User user = userRepository.findByEmail(command.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", "Las credenciales ingresadas son inválidas."));

        user.validarContrasena(command.password(), passwordEncoder);

        if (user.getEstado() != UserStatus.ACTIVO) {
            throw new UnauthorizedException("ACCOUNT_DISABLED", "La cuenta de usuario se encuentra inactiva o suspendida.");
        }

        // HU-SE-10: Si tiene 2FA habilitado, generar desafío y NO emitir JWT final aún
        if (user.isDosFactoresHabilitado()) {
            String desafioToken = UUID.randomUUID().toString();
            String codigoOtp = String.format("%06d", secureRandom.nextInt(1_000_000));
            String codigoHash = passwordEncoder.encode(codigoOtp);

            TwoFactorChallenge challenge = TwoFactorChallenge.crear(user.getId(), desafioToken, codigoHash, 300);
            twoFactorRepository.save(challenge);

            // Simulación de envío por canal seguro (consola/logger en entorno dev)
            System.out.println("[2FA SECURITY OTP] Código generado para " + user.getEmail() + ": " + codigoOtp);

            return AuthResponse.requiere2FA(desafioToken, 300);
        }

        // 2FA no habilitado: Generar token de acceso JWT y registrar sesión
        Set<String> roles = user.obtenerNombresRoles();
        Set<String> permisos = user.obtenerCodigosPermisos();
        TokenGeneratorPort.TokenResult tokenResult = tokenGenerator.generateAccessToken(user, roles, permisos, 86400);

        UserSession session = UserSession.iniciar(
                user.getId(),
                tokenResult.jti(),
                java.time.Instant.now().plusSeconds(tokenResult.expirationSeconds()),
                ipOrigen,
                userAgent
        );
        sessionRepository.save(session);

        AuthResponse.UserSummary summary = new AuthResponse.UserSummary(
                user.getId(),
                user.getEmail(),
                user.getPersona().getNombreCompleto(),
                user.getProveedorAuth().name(),
                roles,
                permisos
        );

        return AuthResponse.autenticado(tokenResult.token(), tokenResult.expirationSeconds(), summary);
    }
}
