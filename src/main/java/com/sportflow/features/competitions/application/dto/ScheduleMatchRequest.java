package com.sportflow.features.competitions.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ScheduleMatchRequest(
        @NotNull(message = "El identificador de la fase es obligatorio.")
        UUID faseId,
        UUID grupoId,
        UUID equipoLocalId,
        UUID equipoVisitanteId,
        @NotNull(message = "La fecha y hora del encuentro es obligatoria.")
        Instant fechaHoraProgramada,
        String escenario,
        UUID partidoOrigen1,
        UUID partidoOrigen2
) {}
