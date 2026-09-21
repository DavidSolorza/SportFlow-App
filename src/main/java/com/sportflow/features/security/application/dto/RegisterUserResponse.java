package com.sportflow.features.security.application.dto;

import java.util.UUID;

public record RegisterUserResponse(
        UUID userId,
        UUID personaId,
        String nombreUsuario,
        String email,
        String estado,
        boolean dosFactoresHabilitado,
        String mensaje
) {}
