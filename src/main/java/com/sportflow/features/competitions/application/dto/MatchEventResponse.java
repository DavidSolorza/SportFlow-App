package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.EventType;

import java.time.Instant;
import java.util.UUID;

public record MatchEventResponse(
        UUID id,
        UUID matchId,
        UUID jugadorId,
        String nombreJugador,
        UUID equipoId,
        String nombreEquipo,
        EventType tipoEvento,
        int minuto,
        String descripcion,
        String estadoValidacion,
        Instant creadoEn
) {}
