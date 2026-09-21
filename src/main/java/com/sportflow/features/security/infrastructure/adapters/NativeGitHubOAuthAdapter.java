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
 * Adaptador Nativo para GitHub OAuth.
 * Consume directamente los endpoints REST de GitHub API v3 sin utilizar SDKs propietarios.
 */
@Component
public class NativeGitHubOAuthAdapter implements OAuthClientPort {

    private static final Logger log = LoggerFactory.getLogger(NativeGitHubOAuthAdapter.class);
    private final NativeHttpClient nativeHttpClient;
    private final ObjectMapper objectMapper;
    private final String gitHubUserUrl;
    private final String gitHubEmailsUrl;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    public NativeGitHubOAuthAdapter(NativeHttpClient nativeHttpClient,
                                    ObjectMapper objectMapper,
                                    @Value("${sportflow.security.oauth.github.userinfo-url:https://api.github.com/user}") String gitHubUserUrl,
                                    @Value("${sportflow.security.oauth.github.emails-url:https://api.github.com/user/emails}") String gitHubEmailsUrl,
                                    @Value("${sportflow.security.oauth.github.client-id:}") String clientId,
                                    @Value("${sportflow.security.oauth.github.client-secret:}") String clientSecret,
                                    @Value("${sportflow.security.oauth.github.token-url:https://github.com/login/oauth/access_token}") String tokenUrl) {
        this.nativeHttpClient = nativeHttpClient;
        this.objectMapper = objectMapper;
        this.gitHubUserUrl = gitHubUserUrl;
        this.gitHubEmailsUrl = gitHubEmailsUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public boolean supports(AuthProvider provider) {
        return provider == AuthProvider.GITHUB;
    }

    @Override
    public OAuthUserProfile verifyAndFetchProfile(AuthProvider provider, String providerToken) {
        String accessToken = resolveAccessToken(providerToken);
        log.info("Verificando token nativo contra endpoint REST de GitHub: {}", gitHubUserUrl);
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + accessToken,
                "Accept", "application/vnd.github+json",
                "User-Agent", "SportFlow-NativeOAuth"
        );

        HttpResponse<String> response = nativeHttpClient.get(gitHubUserUrl, headers);
        if (response.statusCode() != 200) {
            log.warn("GitHub API rechazó el token. Código HTTP: {}, Respuesta: {}", response.statusCode(), response.body());
            throw new UnauthorizedException("OAUTH_PROVIDER_REJECTED", "El token de GitHub es inválido o ha expirado.");
        }

        try {
            JsonNode root = objectMapper.readTree(response.body());
            String providerId = root.has("id") ? root.get("id").asText() : null;
            String name = root.has("name") && !root.get("name").isNull() ? root.get("name").asText() :
                    (root.has("login") ? root.get("login").asText() : "Usuario GitHub");
            String avatarUrl = root.has("avatar_url") ? root.get("avatar_url").asText() : null;
            String email = root.has("email") && !root.get("email").isNull() ? root.get("email").asText() : null;

            // Si el correo es privado en el perfil, consultar endpoint de correos
            if (email == null || email.isBlank()) {
                email = fetchPrimaryEmail(headers);
            }

            if (email == null || email.isBlank()) {
                throw new UnauthorizedException("OAUTH_MISSING_EMAIL", "No se pudo obtener el correo electrónico verificado de la cuenta de GitHub.");
            }

            return new OAuthUserProfile(providerId, email, name, avatarUrl);
        } catch (UnauthorizedException ue) {
            throw ue;
        } catch (Exception e) {
            log.error("Error al procesar la respuesta cruda de GitHub", e);
            throw new UnauthorizedException("OAUTH_PARSE_ERROR", "Error al procesar el perfil devuelto por GitHub: " + e.getMessage());
        }
    }

    private String resolveAccessToken(String tokenOrCode) {
        if (tokenOrCode == null || tokenOrCode.isBlank()) {
            throw new UnauthorizedException("OAUTH_TOKEN_EMPTY", "El token o código de GitHub no puede estar vacío.");
        }
        // Si ya tiene prefijo de token de acceso de GitHub, se usa directamente
        if (tokenOrCode.startsWith("gho_") || tokenOrCode.startsWith("ghp_") || tokenOrCode.startsWith("github_pat_")) {
            return tokenOrCode;
        }

        // Si no tiene client secret configurado, retornamos el token original
        if (clientSecret == null || clientSecret.isBlank()) {
            return tokenOrCode;
        }

        log.info("Canjeando código de autorización OAuth nativamente contra GitHub: {}", tokenUrl);
        try {
            Map<String, String> bodyMap = Map.of(
                    "client_id", clientId != null ? clientId : "",
                    "client_secret", clientSecret,
                    "code", tokenOrCode
            );
            String jsonPayload = objectMapper.writeValueAsString(bodyMap);
            Map<String, String> headers = Map.of(
                    "Accept", "application/json",
                    "Content-Type", "application/json",
                    "User-Agent", "SportFlow-NativeOAuth"
            );

            HttpResponse<String> response = nativeHttpClient.post(tokenUrl, jsonPayload, headers);
            if (response.statusCode() == 200) {
                String body = response.body();
                if (body != null && body.contains("access_token=")) {
                    for (String part : body.split("&")) {
                        if (part.startsWith("access_token=")) {
                            log.info("Código de GitHub canjeado exitosamente (form-urlencoded).");
                            return part.substring("access_token=".length());
                        }
                    }
                }
                JsonNode root = objectMapper.readTree(response.body());
                if (root.has("access_token")) {
                    log.info("Código de GitHub canjeado exitosamente por access_token nativo.");
                    return root.get("access_token").asText();
                } else if (root.has("error_description")) {
                    log.warn("GitHub rechazó el código: {}", root.get("error_description").asText());
                    throw new UnauthorizedException("OAUTH_EXCHANGE_FAILED", root.get("error_description").asText());
                }
            }
            log.warn("Fallo al canjear código contra GitHub. Status: {}, Body: {}", response.statusCode(), response.body());
        } catch (UnauthorizedException ue) {
            throw ue;
        } catch (Exception e) {
            log.error("Excepción al canjear código con GitHub", e);
        }

        return tokenOrCode;
    }

    private String fetchPrimaryEmail(Map<String, String> headers) {
        try {
            HttpResponse<String> response = nativeHttpClient.get(gitHubEmailsUrl, headers);
            if (response.statusCode() == 200) {
                JsonNode array = objectMapper.readTree(response.body());
                if (array.isArray()) {
                    for (JsonNode node : array) {
                        boolean primary = node.has("primary") && node.get("primary").asBoolean();
                        boolean verified = node.has("verified") && node.get("verified").asBoolean();
                        if (primary && verified && node.has("email")) {
                            return node.get("email").asText();
                        }
                    }
                    if (array.size() > 0 && array.get(0).has("email")) {
                        return array.get(0).get("email").asText();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo consultar el endpoint secundario de correos de GitHub: {}", e.getMessage());
        }
        return null;
    }
}
