package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.User;

import java.util.Set;
import java.util.UUID;

public interface TokenGeneratorPort {
    record TokenResult(String token, String jti, long expirationSeconds) {}

    TokenResult generateAccessToken(User user, Set<String> roles, Set<String> permissions);
    TokenResult generateAccessToken(User user, Set<String> roles, Set<String> permissions, long expirationSeconds);
    String extractJti(String token);
    UUID extractUserId(String token);
    String extractUsername(String token);
    boolean validateToken(String token);
}
