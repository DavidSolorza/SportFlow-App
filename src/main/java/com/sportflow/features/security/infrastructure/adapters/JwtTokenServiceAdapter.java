package com.sportflow.features.security.infrastructure.adapters;

import com.sportflow.core.config.JwtProperties;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.TokenGeneratorPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenServiceAdapter implements TokenGeneratorPort {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenServiceAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenResult generateAccessToken(User user, Set<String> roles, Set<String> permissions) {
        return generateAccessToken(user, roles, permissions, jwtProperties.getExpirationSeconds());
    }

    public TokenResult generateAccessToken(User user, Set<String> roles, Set<String> permissions, long expirationSeconds) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (expirationSeconds * 1000));
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .claim("username", user.getNombreUsuario())
                .claim("email", user.getEmail())
                .claim("roles", new ArrayList<>(roles))
                .claim("permissions", new ArrayList<>(permissions))
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        return new TokenResult(token, jti, expirationSeconds);
    }

    @Override
    public String extractJti(String token) {
        try {
            return getClaims(token).getId();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public UUID extractUserId(String token) {
        try {
            return UUID.fromString(getClaims(token).getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String extractUsername(String token) {
        try {
            return getClaims(token).get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            return getClaims(token).get("roles", List.class);
        } catch (JwtException | IllegalArgumentException e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        try {
            return getClaims(token).get("permissions", List.class);
        } catch (JwtException | IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
