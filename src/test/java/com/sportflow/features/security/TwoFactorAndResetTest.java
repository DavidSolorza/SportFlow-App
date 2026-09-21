package com.sportflow.features.security;

import com.sportflow.features.security.application.dto.AuthResponse;
import com.sportflow.features.security.application.dto.PasswordResetDTOs;
import com.sportflow.features.security.application.dto.Verify2FACommand;
import com.sportflow.features.security.application.usecase.ConfirmPasswordResetUseCase;
import com.sportflow.features.security.application.usecase.VerifyTwoFactorUseCase;
import com.sportflow.features.security.domain.model.PasswordResetRequest;
import com.sportflow.features.security.domain.model.Person;
import com.sportflow.features.security.domain.model.TwoFactorChallenge;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TwoFactorAndResetTest {

    private TwoFactorRepositoryPort twoFactorRepository;
    private UserRepositoryPort userRepository;
    private PasswordEncoderPort passwordEncoder;
    private TokenGeneratorPort tokenGenerator;
    private SessionRepositoryPort sessionRepository;
    private PasswordResetRepositoryPort passwordResetRepository;

    private VerifyTwoFactorUseCase verifyTwoFactorUseCase;
    private ConfirmPasswordResetUseCase confirmPasswordResetUseCase;

    @BeforeEach
    void setUp() {
        twoFactorRepository = Mockito.mock(TwoFactorRepositoryPort.class);
        userRepository = Mockito.mock(UserRepositoryPort.class);
        passwordEncoder = Mockito.mock(PasswordEncoderPort.class);
        tokenGenerator = Mockito.mock(TokenGeneratorPort.class);
        sessionRepository = Mockito.mock(SessionRepositoryPort.class);
        passwordResetRepository = Mockito.mock(PasswordResetRepositoryPort.class);

        verifyTwoFactorUseCase = new VerifyTwoFactorUseCase(
                twoFactorRepository,
                userRepository,
                passwordEncoder,
                tokenGenerator,
                sessionRepository
        );

        confirmPasswordResetUseCase = new ConfirmPasswordResetUseCase(
                passwordResetRepository,
                userRepository,
                passwordEncoder,
                sessionRepository
        );
    }

    @Test
    @DisplayName("HU-SE-10: Valida código 2FA exitosamente y activa sesión interna")
    void testVerifyTwoFactorSuccess() {
        String desafioToken = "desafio-token-abc";
        String plainCode = "654321";
        String hashCode = "$2a$12$hash";

        Person p = Person.crear("CC", "123", "Carlos", "Gómez", "carlos@sportflow.com", null);
        User u = User.crearLocal(p, "cgomez", "carlos@sportflow.com", "hash");
        UUID userId = u.getId();

        TwoFactorChallenge challenge = TwoFactorChallenge.crear(userId, desafioToken, hashCode, 300);
        when(twoFactorRepository.findByDesafioToken(desafioToken)).thenReturn(Optional.of(challenge));
        when(passwordEncoder.matches(plainCode, hashCode)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(u));

        when(tokenGenerator.generateAccessToken(any(), any(), any(), anyLong()))
                .thenReturn(new TokenGeneratorPort.TokenResult("jwt-2fa-token", "jti-99", 86400));

        AuthResponse response = verifyTwoFactorUseCase.execute(
                new Verify2FACommand(desafioToken, plainCode), "127.0.0.1", "JUnit-Agent");

        assertNotNull(response);
        assertEquals("AUTENTICADO", response.estado());
        assertEquals("jwt-2fa-token", response.tokenAcceso());
        assertTrue(challenge.isUtilizado());

        verify(twoFactorRepository, times(1)).save(challenge);
        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("HU-SE-09: Restablecimiento de contraseña invalida todas las sesiones activas")
    void testConfirmPasswordResetRevokesAllSessions() {
        String token = "reset-token-xyz";

        Person p = Person.crear("CC", "123", "Carlos", "Gómez", "carlos@sportflow.com", null);
        User u = User.crearLocal(p, "cgomez", "carlos@sportflow.com", "oldHash");
        UUID userId = u.getId();

        PasswordResetRequest resetRequest = PasswordResetRequest.crear(userId, token, 15);
        when(passwordResetRepository.findByTokenHash(token)).thenReturn(Optional.of(resetRequest));
        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("NewSecretPassword123!#")).thenReturn("newHash");

        PasswordResetDTOs.SimpleMessageResponse response = confirmPasswordResetUseCase.execute(
                new PasswordResetDTOs.ConfirmResetCommand(token, "NewSecretPassword123!#"));

        assertNotNull(response);
        assertTrue(resetRequest.isUtilizado());
        assertEquals("newHash", u.getPasswordHash());

        verify(sessionRepository, times(1)).revokeAllByUserId(userId);
        verify(userRepository, times(1)).save(u);
    }
}
