package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.UnauthorizedException;
import com.sportflow.features.security.application.dto.AuthResponse;
import com.sportflow.features.security.domain.model.*;
import com.sportflow.features.security.domain.ports.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticateOAuthUseCase {

    private final List<OAuthClientPort> oAuthClients;
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final SessionRepositoryPort sessionRepository;

    public AuthenticateOAuthUseCase(List<OAuthClientPort> oAuthClients,
                                   UserRepositoryPort userRepository,
                                   RoleRepositoryPort roleRepository,
                                   TokenGeneratorPort tokenGenerator,
                                   SessionRepositoryPort sessionRepository) {
        this.oAuthClients = oAuthClients;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenGenerator = tokenGenerator;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public AuthResponse execute(AuthProvider provider, String providerToken, String ipOrigen, String userAgent) {
        OAuthClientPort clientPort = oAuthClients.stream()
                .filter(client -> client.supports(provider))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("UNSUPPORTED_OAUTH_PROVIDER", "Proveedor OAuth no soportado: " + provider));

        OAuthClientPort.OAuthUserProfile profile = clientPort.verifyAndFetchProfile(provider, providerToken);

        String email = profile.email().trim().toLowerCase();
        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            // HU-SE-08: Si ya se utilizó el correo electrónico por el sistema de autenticación básico (LOCAL), se rechaza
            if (user.getProveedorAuth() == AuthProvider.LOCAL) {
                throw new ConflictException("ACCOUNT_EXISTS_WITH_CREDENTIALS",
                        "El correo electrónico ya se encuentra registrado con autenticación tradicional por contraseña. Ingrese con sus credenciales.");
            }
        } else {
            // Usuario nuevo por OAuth: auto-provisionamiento
            String[] names = profile.name() != null ? profile.name().split(" ", 2) : new String[]{"Usuario", "OAuth"};
            String nombres = names[0];
            String apellidos = names.length > 1 ? names[1] : "Deportivo";

            Person persona = Person.crear(
                    "OAUTH",
                    "OAUTH-" + UUID.randomUUID().toString().substring(0, 8),
                    nombres,
                    apellidos,
                    email,
                    null
            );

            String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
            String username = baseUsername;
            int counter = 1;
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + counter++;
            }

            user = User.crearOAuth(persona, username, email, provider, profile.providerId());
            if (profile.avatarUrl() != null && user.getPerfil() != null) {
                user.getPerfil().actualizar(null, profile.avatarUrl(), "Registrado mediante " + provider);
            }

            // Asignar rol por defecto AFICIONADO
            Optional<Role> defaultRole = roleRepository.findByNombre("AFICIONADO");
            defaultRole.ifPresent(user::asignarRol);

            user = userRepository.save(user);
        }

        if (user.getEstado() != UserStatus.ACTIVO) {
            throw new UnauthorizedException("ACCOUNT_DISABLED", "La cuenta se encuentra inactiva o suspendida.");
        }

        // Generar JWT y sesión interna (OAuth NO requiere 2FA administrado por la plataforma según HU-SE-08)
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
