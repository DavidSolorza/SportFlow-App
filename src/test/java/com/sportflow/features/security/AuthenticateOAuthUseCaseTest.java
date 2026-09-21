package com.sportflow.features.security;

import com.sportflow.core.errors.ConflictException;
import com.sportflow.features.security.application.dto.AuthResponse;
import com.sportflow.features.security.application.usecase.AuthenticateOAuthUseCase;
import com.sportflow.features.security.domain.model.AuthProvider;
import com.sportflow.features.security.domain.model.Person;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticateOAuthUseCaseTest {

    private OAuthClientPort googleClient;
    private UserRepositoryPort userRepository;
    private RoleRepositoryPort roleRepository;
    private TokenGeneratorPort tokenGenerator;
    private SessionRepositoryPort sessionRepository;
    private AuthenticateOAuthUseCase useCase;

    @BeforeEach
    void setUp() {
        googleClient = Mockito.mock(OAuthClientPort.class);
        userRepository = Mockito.mock(UserRepositoryPort.class);
        roleRepository = Mockito.mock(RoleRepositoryPort.class);
        tokenGenerator = Mockito.mock(TokenGeneratorPort.class);
        sessionRepository = Mockito.mock(SessionRepositoryPort.class);

        when(googleClient.supports(AuthProvider.GOOGLE)).thenReturn(true);

        useCase = new AuthenticateOAuthUseCase(
                List.of(googleClient),
                userRepository,
                roleRepository,
                tokenGenerator,
                sessionRepository
        );
    }

    @Test
    @DisplayName("HU-SE-08: Debe rechazar OAuth si el correo ya está registrado con autenticación tradicional")
    void shouldRejectOAuthWhenEmailExistsWithLocalPassword() {
        String testEmail = "usuario.local@sportflow.com";
        when(googleClient.verifyAndFetchProfile(AuthProvider.GOOGLE, "valid-token"))
                .thenReturn(new OAuthClientPort.OAuthUserProfile("google-123", testEmail, "Usuario Local", null));

        Person person = Person.crear("CC", "123456", "Usuario", "Local", testEmail, null);
        User localUser = User.crearLocal(person, "ulocal", testEmail, "$2a$12$hashedPassword");

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(localUser));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                useCase.execute(AuthProvider.GOOGLE, "valid-token", "127.0.0.1", "JUnit-Test"));

        assertEquals("ACCOUNT_EXISTS_WITH_CREDENTIALS", ex.getCode());
        verify(tokenGenerator, never()).generateAccessToken(any(), any(), any(), anyLong());
    }

    @Test
    @DisplayName("HU-SE-08: Auto-provisiona un nuevo usuario cuando ingresa por primera vez con Google OAuth")
    void shouldProvisionNewUserOnFirstOAuthLogin() {
        String testEmail = "nuevo.oauth@sportflow.com";
        when(googleClient.verifyAndFetchProfile(AuthProvider.GOOGLE, "valid-google-token"))
                .thenReturn(new OAuthClientPort.OAuthUserProfile("google-999", testEmail, "Nuevo Atleta", "https://photo.url"));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenGenerator.generateAccessToken(any(), any(), any(), anyLong()))
                .thenReturn(new TokenGeneratorPort.TokenResult("jwt-token-xyz", "jti-123", 86400));

        AuthResponse response = useCase.execute(AuthProvider.GOOGLE, "valid-google-token", "127.0.0.1", "JUnit-Test");

        assertNotNull(response);
        assertEquals("AUTENTICADO", response.estado());
        assertEquals("jwt-token-xyz", response.tokenAcceso());
        assertEquals("GOOGLE", response.usuario().proveedorAuth());
        assertEquals(testEmail, response.usuario().email());

        verify(userRepository, times(1)).save(any(User.class));
        verify(sessionRepository, times(1)).save(any());
    }
}
