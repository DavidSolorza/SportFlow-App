package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.MatchStatus;

import java.time.Instant;
import java.util.UUID;

public record MatchResponse(
        UUID id,
        UUID faseId,
        UUID grupoId,
        UUID equipoLocalId,
        String nombreEquipoLocal,
        UUID equipoVisitanteId,
        String nombreEquipoVisitante,
        Instant fechaHoraProgramada,
        String escenario,
        MatchStatus estado,
        boolean resultadoConfirmado,
        MatchResultResponse resultado
) {}
