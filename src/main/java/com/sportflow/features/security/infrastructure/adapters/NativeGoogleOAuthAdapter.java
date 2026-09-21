package com.sportflow.features.security.infrastructure.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportflow.core.errors.UnauthorizedException;
import com.sportflow.core.http.NativeHttpClient;
import com.sportflow.features.security.domain.model.AuthProvider;
import com.sportflow.features.security.domain.ports.OAuthClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Adaptador Nativo para Google OAuth2.
 * Consume directamente el endpoint REST de Google Userinfo sin utilizar SDKs propietarios.
 */
@Component
public class NativeGoogleOAuthAdapter implements OAuthClientPort {

    private static final Logger log = LoggerFactory.getLogger(NativeGoogleOAuthAdapter.class);
    private final NativeHttpClient nativeHttpClient;
    private final ObjectMapper objectMapper;
    private final String googleUserinfoUrl;

    public NativeGoogleOAuthAdapter(NativeHttpClient nativeHttpClient,
                                    ObjectMapper objectMapper,
                                    @Value("${sportflow.security.oauth.google.userinfo-url:https://www.googleapis.com/oauth2/v3/userinfo}") String googleUserinfoUrl) {
        this.nativeHttpClient = nativeHttpClient;
        this.objectMapper = objectMapper;
        this.googleUserinfoUrl = googleUserinfoUrl;
    }

    @Override
    public boolean supports(AuthProvider provider) {
        return provider == AuthProvider.GOOGLE;
    }

    @Override
    public OAuthUserProfile verifyAndFetchProfile(AuthProvider provider, String providerToken) {
        log.info("Verificando token nativo contra endpoint REST de Google: {}", googleUserinfoUrl);
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + providerToken,
                "Accept", "application/json"
        );

        HttpResponse<String> response = nativeHttpClient.get(googleUserinfoUrl, headers);
        if (response.statusCode() != 200) {
            log.warn("Google OAuth rechazó el token. Código HTTP: {}, Respuesta: {}", response.statusCode(), response.body());
            throw new UnauthorizedException("OAUTH_PROVIDER_REJECTED", "El token de Google es inválido o ha expirado.");
        }

        try {
            JsonNode root = objectMapper.readTree(response.body());
            String providerId = root.has("sub") ? root.get("sub").asText() : null;
            String email = root.has("email") ? root.get("email").asText() : null;
            String name = root.has("name") ? root.get("name").asText() : "Usuario Google";
            String avatarUrl = root.has("picture") ? root.get("picture").asText() : null;

            if (email == null || email.isBlank()) {
                throw new UnauthorizedException("OAUTH_MISSING_EMAIL", "No se pudo obtener el correo electrónico del perfil de Google.");
            }

            return new OAuthUserProfile(providerId, email, name, avatarUrl);
        } catch (Exception e) {
            log.error("Error al deserializar respuesta cruda de Google", e);
            throw new UnauthorizedException("OAUTH_PARSE_ERROR", "Error al procesar el perfil devuelto por Google: " + e.getMessage());
        }
    }
}
