package com.sportflow.features.security.application.dto;

import java.util.Set;
import java.util.UUID;

public record AuthResponse(
        String estado,
        boolean requiereSegundoFactor,
        String desafioToken,
        String tokenAcceso,
        String tipoToken,
        Long expiraEnSegundos,
        UserSummary usuario,
        String mensaje
) {
    public record UserSummary(
            UUID id,
            String email,
            String nombreCompleto,
            String proveedorAuth,
            Set<String> roles,
            Set<String> permisos
    ) {}

    public static AuthResponse autenticado(String token, long expiraEn, UserSummary user) {
        return new AuthResponse("AUTENTICADO", false, null, token, "Bearer", expiraEn, user, null);
    }

    public static AuthResponse requiere2FA(String desafioToken, long expiraEn) {
        return new AuthResponse("PENDIENTE_2FA", true, desafioToken, null, null, expiraEn, null,
                "Se requiere validación del segundo factor de autenticación.");
    }
}
