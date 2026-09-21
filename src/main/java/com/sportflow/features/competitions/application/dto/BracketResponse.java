package com.sportflow.features.competitions.application.dto;

import java.util.UUID;

public record BracketResponse(
        UUID id,
        UUID faseId,
        String nombre,
        int ronda,
        int orden,
        UUID partidoId,
        UUID ganadorEquipoId
) {}
