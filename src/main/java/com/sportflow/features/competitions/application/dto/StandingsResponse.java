package com.sportflow.features.competitions.application.dto;

import java.time.Instant;
import java.util.UUID;

public record StandingsResponse(
        UUID id,
        UUID faseId,
        UUID grupoId,
        UUID equipoId,
        String nombreEquipo,
        int posicion,
        int partidosJugados,
        int victorias,
        int empates,
        int derrotas,
        int golesFavor,
        int golesContra,
        int diferenciaGoles,
        int puntos,
        Instant fechaCalculo
) {}
