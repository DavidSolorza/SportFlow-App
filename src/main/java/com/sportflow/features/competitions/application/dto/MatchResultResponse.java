package com.sportflow.features.competitions.application.dto;

import java.time.Instant;
import java.util.UUID;

public record MatchResultResponse(
        UUID id,
        UUID matchId,
        int golesLocal,
        int golesVisitante,
        Instant fechaConfirmacion,
        String confirmadoPor,
        String observaciones
) {}
