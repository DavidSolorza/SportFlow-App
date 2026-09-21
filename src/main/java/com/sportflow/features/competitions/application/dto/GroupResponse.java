package com.sportflow.features.competitions.application.dto;

import java.util.Set;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        UUID faseId,
        String nombre,
        int orden,
        Set<UUID> equipoIds
) {}
