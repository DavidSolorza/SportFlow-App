package com.sportflow.features.security.infrastructure.security;

import com.sportflow.features.security.domain.model.UserSession;
import com.sportflow.features.security.domain.ports.SessionRepositoryPort;
import com.sportflow.features.security.infrastructure.adapters.JwtTokenServiceAdapter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenServiceAdapter jwtTokenService;
    private final SessionRepositoryPort sessionRepository;

    public JwtAuthenticationFilter(JwtTokenServiceAdapter jwtTokenService,
                                   SessionRepositoryPort sessionRepository) {
        this.jwtTokenService = jwtTokenService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtTokenService.validateToken(token)) {
                String jti = jwtTokenService.extractJti(token);
                Optional<UserSession> sessionOpt = sessionRepository.findByTokenJti(jti);

                // Validar que la sesión no haya sido revocada (HU-SE-08 / HU-SE-09)
                if (sessionOpt.isPresent() && sessionOpt.get().esValida()) {
                    UUID userId = jwtTokenService.extractUserId(token);
                    String username = jwtTokenService.extractUsername(token);
                    List<String> roles = jwtTokenService.extractRoles(token);
                    List<String> permissions = jwtTokenService.extractPermissions(token);

                    UserPrincipal userPrincipal = UserPrincipal.create(userId, username, username, roles, permissions);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
