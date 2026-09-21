package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.core.errors.UnauthorizedException;
import com.sportflow.features.security.application.dto.AuthResponse;
import com.sportflow.features.security.application.dto.Verify2FACommand;
import com.sportflow.features.security.domain.model.TwoFactorChallenge;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.model.UserSession;
import com.sportflow.features.security.domain.ports.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class VerifyTwoFactorUseCase {

    private final TwoFactorRepositoryPort twoFactorRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final SessionRepositoryPort sessionRepository;

    public VerifyTwoFactorUseCase(TwoFactorRepositoryPort twoFactorRepository,
                                  UserRepositoryPort userRepository,
                                  PasswordEncoderPort passwordEncoder,
                                  TokenGeneratorPort tokenGenerator,
                                  SessionRepositoryPort sessionRepository) {
        this.twoFactorRepository = twoFactorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public AuthResponse execute(Verify2FACommand command, String ipOrigen, String userAgent) {
        TwoFactorChallenge challenge = twoFactorRepository.findByDesafioToken(command.desafioToken())
                .orElseThrow(() -> new BusinessRuleException("INVALID_2FA_TOKEN", "El desafío de segundo factor es inexistente o inválido."));

        if (!challenge.esValido()) {
            throw new BusinessRuleException("EXPIRED_OR_USED_2FA_CODE", "El código de verificación ha expirado o ya fue utilizado.");
        }

        if (!passwordEncoder.matches(command.codigo(), challenge.getCodigoHash())) {
            throw new UnauthorizedException("INCORRECT_2FA_CODE", "El código de verificación de 6 dígitos ingresado es incorrecto.");
        }

        challenge.marcarUtilizado();
        twoFactorRepository.save(challenge);

        User user = userRepository.findById(challenge.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario no encontrado para completar la sesión."));

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
